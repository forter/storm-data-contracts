package com.forter.contracts.testng;

import javax.validation.constraints.NotNull;

/**
 *  List of {@link MyBolt}
 */
public class MyBoltListItem {

    @NotNull
    public int x;

    @NotNull
    public String y;
}
