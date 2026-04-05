package org.ndviet.library.webui.selenium.bidi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.bidi.BiDi;
import org.openqa.selenium.bidi.BiDiSessionStatus;
import org.openqa.selenium.bidi.HasBiDi;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.CreateContextParameters;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;
import org.openqa.selenium.bidi.module.Browser;
import org.openqa.selenium.bidi.log.JavascriptLogEntry;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.CacheBehavior;
import org.openqa.selenium.bidi.network.FetchError;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.RequestData;
import org.openqa.selenium.bidi.network.ResponseData;
import org.openqa.selenium.bidi.network.ResponseDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BooleanSupplier;

public final class WebUiBiDi {
    private static final Logger LOGGER = LogManager.getLogger(WebUiBiDi.class);
    private static final long POLL_INTERVAL_MS = 100L;

    private WebUiBiDi() {
    }

    public static boolean isBiDiSupported(WebDriver driver) {
        if (!(driver instanceof HasBiDi)) {
            return false;
        }
        try {
            ((HasBiDi) driver).getBiDi();
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("BiDi is not available for the active WebDriver session.", e);
            return false;
        }
    }

    public static BiDiSessionStatus getBiDiSessionStatus(WebDriver driver) {
        return getBiDi(driver).getBidiSessionStatus();
    }

    public static String getCurrentBrowsingContextId(WebDriver driver) {
        return driver.getWindowHandle();
    }

    public static String createUserContext(WebDriver driver) {
        return new Browser(driver).createUserContext();
    }

    public static List<String> getUserContexts(WebDriver driver) {
        return new Browser(driver).getUserContexts();
    }

    public static void removeUserContext(String userContextId, WebDriver driver) {
        new Browser(driver).removeUserContext(userContextId);
    }

    public static BrowsingContext createBrowsingContext(WebDriver driver, WindowType type) {
        return createBrowsingContext(type, null, getCurrentBrowsingContextId(driver), driver);
    }

    public static BrowsingContext createBrowsingContext(WindowType type, String userContextId, WebDriver driver) {
        return createBrowsingContext(type, userContextId, getCurrentBrowsingContextId(driver), driver);
    }

    public static BrowsingContext createBrowsingContext(
            WindowType type, String userContextId, String referenceBrowsingContextId, WebDriver driver) {
        CreateContextParameters parameters = new CreateContextParameters(type);
        if (referenceBrowsingContextId != null && !referenceBrowsingContextId.isBlank()) {
            parameters.referenceContext(referenceBrowsingContextId);
        }
        if (userContextId != null && !userContextId.isBlank()) {
            parameters.userContext(userContextId);
        }
        BrowsingContext browsingContext = new BrowsingContext(driver, parameters);
        driver.switchTo().window(browsingContext.getId());
        return browsingContext;
    }

    public static void switchToBrowsingContext(String browsingContextId, WebDriver driver) {
        driver.switchTo().window(browsingContextId);
    }

    public static void closeBrowsingContext(String browsingContextId, WebDriver driver) {
        new BrowsingContext(driver, browsingContextId).close();
    }

    public static BiDiLogCollector startLogCollector(WebDriver driver) {
        return new BiDiLogCollector(getCurrentBrowsingContextId(driver), driver);
    }

    public static BiDiLogCollector startLogCollector(String browsingContextId, WebDriver driver) {
        return new BiDiLogCollector(browsingContextId, driver);
    }

    public static BiDiLogCollector startLogCollector(Set<String> browsingContextIds, WebDriver driver) {
        return new BiDiLogCollector(browsingContextIds, driver);
    }

    public static BiDiNetworkCollector startNetworkCollector(WebDriver driver) {
        return new BiDiNetworkCollector(getCurrentBrowsingContextId(driver), driver);
    }

    public static BiDiNetworkCollector startNetworkCollector(String browsingContextId, WebDriver driver) {
        return new BiDiNetworkCollector(browsingContextId, driver);
    }

    public static BiDiNetworkCollector startNetworkCollector(Set<String> browsingContextIds, WebDriver driver) {
        return new BiDiNetworkCollector(browsingContextIds, driver);
    }

    private static BiDi getBiDi(WebDriver driver) {
        if (!(driver instanceof HasBiDi)) {
            throw new IllegalStateException("WebDriver instance does not implement HasBiDi.");
        }
        return ((HasBiDi) driver).getBiDi();
    }

    private static boolean waitFor(BooleanSupplier condition, int timeoutInSeconds) {
        long timeoutMs = Math.max(timeoutInSeconds, 0) * 1000L;
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() <= deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return condition.getAsBoolean();
    }

    public static final class BiDiLogCollector implements AutoCloseable {
        private final LogInspector logInspector;
        private final List<ConsoleLogMessage> consoleMessages = new CopyOnWriteArrayList<>();
        private final List<JavaScriptLogMessage> javascriptMessages = new CopyOnWriteArrayList<>();
        private final List<JavaScriptLogMessage> javascriptErrors = new CopyOnWriteArrayList<>();

        private BiDiLogCollector(WebDriver driver) {
            this.logInspector = new LogInspector(driver);
            subscribe();
        }

        private BiDiLogCollector(String browsingContextId, WebDriver driver) {
            this.logInspector = new LogInspector(browsingContextId, driver);
            subscribe();
        }

        private BiDiLogCollector(Set<String> browsingContextIds, WebDriver driver) {
            this.logInspector = new LogInspector(browsingContextIds, driver);
            subscribe();
        }

        private void subscribe() {
            this.logInspector.onConsoleEntry(
                    entry -> this.consoleMessages.add(ConsoleLogMessage.from(entry)));
            this.logInspector.onJavaScriptLog(
                    entry -> this.javascriptMessages.add(JavaScriptLogMessage.from(entry)));
            this.logInspector.onJavaScriptException(
                    entry -> this.javascriptErrors.add(JavaScriptLogMessage.from(entry)));
        }

        public List<ConsoleLogMessage> getConsoleMessages() {
            return List.copyOf(consoleMessages);
        }

        public List<JavaScriptLogMessage> getJavaScriptMessages() {
            return List.copyOf(javascriptMessages);
        }

        public List<JavaScriptLogMessage> getJavaScriptErrors() {
            return List.copyOf(javascriptErrors);
        }

        public boolean hasJavaScriptErrors() {
            return !javascriptErrors.isEmpty();
        }

        public boolean waitForConsoleMessageContains(String expectedText, int timeoutInSeconds) {
            Objects.requireNonNull(expectedText, "expectedText");
            return waitFor(
                    () -> consoleMessages.stream().anyMatch(log -> log.text().contains(expectedText)),
                    timeoutInSeconds);
        }

        public boolean waitForJavaScriptErrorContains(String expectedText, int timeoutInSeconds) {
            Objects.requireNonNull(expectedText, "expectedText");
            return waitFor(
                    () -> javascriptErrors.stream().anyMatch(log -> log.text().contains(expectedText)),
                    timeoutInSeconds);
        }

        public void assertNoJavaScriptErrors() {
            if (!javascriptErrors.isEmpty()) {
                throw new RuntimeException("JavaScript errors were captured via BiDi: " + javascriptErrors);
            }
        }

        public void clear() {
            consoleMessages.clear();
            javascriptMessages.clear();
            javascriptErrors.clear();
        }

        @Override
        public void close() {
            this.logInspector.close();
        }
    }

    public static final class BiDiNetworkCollector implements AutoCloseable {
        private final Network network;
        private final List<NetworkRequestEvent> requests = new CopyOnWriteArrayList<>();
        private final List<NetworkResponseEvent> responses = new CopyOnWriteArrayList<>();
        private final List<NetworkErrorEvent> errors = new CopyOnWriteArrayList<>();
        private final List<String> interceptIds = new CopyOnWriteArrayList<>();

        private BiDiNetworkCollector(WebDriver driver) {
            this.network = new Network(driver);
            subscribe();
        }

        private BiDiNetworkCollector(String browsingContextId, WebDriver driver) {
            this.network = new Network(browsingContextId, driver);
            subscribe();
        }

        private BiDiNetworkCollector(Set<String> browsingContextIds, WebDriver driver) {
            this.network = new Network(browsingContextIds, driver);
            subscribe();
        }

        private void subscribe() {
            this.network.onBeforeRequestSent(
                    entry -> this.requests.add(NetworkRequestEvent.from(entry)));
            this.network.onResponseCompleted(
                    entry -> this.responses.add(NetworkResponseEvent.from(entry)));
            this.network.onFetchError(
                    entry -> this.errors.add(NetworkErrorEvent.from(entry)));
        }

        public String addIntercept(InterceptPhase phase, String urlStringPattern) {
            AddInterceptParameters parameters = new AddInterceptParameters(phase);
            if (urlStringPattern != null && !urlStringPattern.isBlank()) {
                parameters.urlStringPattern(urlStringPattern);
            }
            String interceptId = this.network.addIntercept(parameters);
            this.interceptIds.add(interceptId);
            return interceptId;
        }

        public void removeIntercept(String interceptId) {
            this.network.removeIntercept(interceptId);
            this.interceptIds.remove(interceptId);
        }

        public void removeAllIntercepts() {
            List<String> currentIntercepts = new ArrayList<>(interceptIds);
            currentIntercepts.forEach(this::removeIntercept);
        }

        public long enableBasicAuthentication(String username, String password) {
            UsernameAndPassword credentials = new UsernameAndPassword(username, password);
            return this.network.onAuthRequired(
                    response -> this.network.continueWithAuth(response.getRequest().getRequestId(), credentials));
        }

        public void disableCache() {
            this.network.setCacheBehavior(CacheBehavior.BYPASS);
        }

        public void enableDefaultCache() {
            this.network.setCacheBehavior(CacheBehavior.DEFAULT);
        }

        public List<NetworkRequestEvent> getRequests() {
            return List.copyOf(requests);
        }

        public List<NetworkResponseEvent> getResponses() {
            return List.copyOf(responses);
        }

        public List<NetworkErrorEvent> getErrors() {
            return List.copyOf(errors);
        }

        public List<NetworkResponseEvent> getFailedResponses() {
            return responses.stream().filter(response -> response.statusCode() >= 400).toList();
        }

        public boolean waitForRequestUrlContains(String expectedUrlPart, int timeoutInSeconds) {
            Objects.requireNonNull(expectedUrlPart, "expectedUrlPart");
            return waitFor(
                    () -> requests.stream().anyMatch(request -> request.url().contains(expectedUrlPart)),
                    timeoutInSeconds);
        }

        public boolean waitForResponseUrlContains(String expectedUrlPart, int timeoutInSeconds) {
            Objects.requireNonNull(expectedUrlPart, "expectedUrlPart");
            return waitFor(
                    () -> responses.stream().anyMatch(response -> response.url().contains(expectedUrlPart)),
                    timeoutInSeconds);
        }

        public boolean waitForResponseStatus(int expectedStatusCode, int timeoutInSeconds) {
            return waitFor(
                    () -> responses.stream().anyMatch(response -> response.statusCode() == expectedStatusCode),
                    timeoutInSeconds);
        }

        public void clear() {
            requests.clear();
            responses.clear();
            errors.clear();
        }

        @Override
        public void close() {
            removeAllIntercepts();
            this.network.close();
        }
    }

    public record ConsoleLogMessage(
            String level,
            String method,
            String text,
            long timestamp,
            String realm,
            String browsingContext,
            List<String> arguments) {

        private static ConsoleLogMessage from(ConsoleLogEntry entry) {
            List<String> argumentValues = entry.getArgs().stream().map(String::valueOf).toList();
            return new ConsoleLogMessage(
                    entry.getLevel().toString(),
                    entry.getMethod(),
                    entry.getText(),
                    entry.getTimestamp(),
                    entry.getSource().getRealm(),
                    entry.getSource().getBrowsingContext().orElse(null),
                    List.copyOf(argumentValues));
        }
    }

    public record JavaScriptLogMessage(
            String level,
            String type,
            String text,
            long timestamp,
            String realm,
            String browsingContext) {

        private static JavaScriptLogMessage from(JavascriptLogEntry entry) {
            return new JavaScriptLogMessage(
                    entry.getLevel().toString(),
                    entry.getType(),
                    entry.getText(),
                    entry.getTimestamp(),
                    entry.getSource().getRealm(),
                    entry.getSource().getBrowsingContext().orElse(null));
        }
    }

    public record NetworkRequestEvent(
            String requestId,
            String method,
            String url,
            String browsingContext,
            String navigationId,
            long timestamp,
            long redirectCount,
            boolean blocked) {

        private static NetworkRequestEvent from(BeforeRequestSent entry) {
            RequestData request = entry.getRequest();
            return new NetworkRequestEvent(
                    request.getRequestId(),
                    request.getMethod(),
                    request.getUrl(),
                    entry.getBrowsingContextId(),
                    entry.getNavigationId(),
                    entry.getTimestamp(),
                    entry.getRedirectCount(),
                    entry.isBlocked());
        }
    }

    public record NetworkResponseEvent(
            String requestId,
            String method,
            String url,
            int statusCode,
            String statusText,
            String mimeType,
            long bytesReceived,
            String browsingContext,
            String navigationId,
            long timestamp,
            boolean fromCache) {

        private static NetworkResponseEvent from(ResponseDetails entry) {
            RequestData request = entry.getRequest();
            ResponseData response = entry.getResponseData();
            return new NetworkResponseEvent(
                    request.getRequestId(),
                    request.getMethod(),
                    response.getUrl(),
                    response.getStatus(),
                    response.getStatusText(),
                    response.getMimeType(),
                    response.getBytesReceived(),
                    entry.getBrowsingContextId(),
                    entry.getNavigationId(),
                    entry.getTimestamp(),
                    response.isFromCache());
        }
    }

    public record NetworkErrorEvent(
            String requestId,
            String method,
            String url,
            String errorText,
            String browsingContext,
            String navigationId,
            long timestamp) {

        private static NetworkErrorEvent from(FetchError entry) {
            RequestData request = entry.getRequest();
            return new NetworkErrorEvent(
                    request.getRequestId(),
                    request.getMethod(),
                    request.getUrl(),
                    entry.getErrorText(),
                    entry.getBrowsingContextId(),
                    entry.getNavigationId(),
                    entry.getTimestamp());
        }
    }
}
