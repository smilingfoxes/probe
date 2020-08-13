package org.example;

import java.lang.instrument.Instrumentation;

public class jdbcPremain {
    public static String agentargs;

    public static String getAgentargs() {
        return agentargs;
    }

    public static void setAgentargs(String agentargs) {
        jdbcPremain.agentargs = agentargs;
    }

    public static void premain(String agentArgs, Instrumentation inst){
        System.out.println("premain is loading...");
        agentargs = agentArgs;
        inst.addTransformer(new Test(),true);
    }
}
