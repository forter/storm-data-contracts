package com.forter.contracts.testng;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Output of {@link MyBolt}
 */
public class MyBoltOutput {
    @NotNull
    public String z;

    public List<MyBoltList> list;
}
