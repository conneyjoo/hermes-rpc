package com.xhtech.hermes.core.netty.session;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by louis on 2020/9/21.
 */
public class SessionEventTypeTest {
    @Test
    public void isCreated() throws Exception {
        Assert.assertTrue("created type check failed",SessionEventType.CREATED.isCreated());
    }

    @Test
    public void isDestroy() throws Exception {
        Assert.assertTrue("destroy type check failed",SessionEventType.DESTROY.isDestroy());
    }

    @Test
    public void isActive() throws Exception {
        Assert.assertTrue("Active type check failed",SessionEventType.ACTIVE.isActive());
    }

}