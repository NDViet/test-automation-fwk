package com.ndviet;

import com.ndviet.ssh.SshConnection;
import com.ndviet.ssh.SshResponse;
import com.ndviet.ssh.SshUtils;

import java.util.LinkedHashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        LinkedHashMap map = YamlUtils.readYaml("libraries/utilities/src/main/resources/sample.yaml");
        System.out.println("YAML content: " + map);
        System.out.println("YAML content: " + YamlUtils.getValueAsString(map, "homePage.level1.level2.level3.value"));
        SshConnection conn = new SshConnection("ndviet", "welcome", "127.0.0.1", 22);
        SshResponse response = SshUtils.runCommand(conn, "sudo pip install robotframework", 600);
        System.out.println("Code: " + response.getReturnCode());
        System.out.println("Response: " + response.getJoinedOutput());
    }
}