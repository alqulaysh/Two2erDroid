package com.se491.app.two2er.GetUsers;


import com.se491.app.two2er.GetUsers.GetUsers;
import com.se491.app.two2er.HelperObjects.UserObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by pazra on 5/13/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class DistanceRefreshStrategyTest {

    @Test
    public void constructorTest() {
        DistanceRefreshStrategy strat = new DistanceRefreshStrategy();
        assertEquals(100.0, strat.getDistance());

        strat = new DistanceRefreshStrategy(100);
        assertEquals(100.0, strat.getDistance());

        strat = new DistanceRefreshStrategy(100, 100, 100);
        assertEquals(100.0, strat.getDistance());
    }

    @Test
    public void runTest() {
        DistanceRefreshStrategy strat = spy(DistanceRefreshStrategy.class);

        doCallRealMethod().when(strat).run();
        HashMap<String, UserObject> users = strat.getUsersList();
    }

}
