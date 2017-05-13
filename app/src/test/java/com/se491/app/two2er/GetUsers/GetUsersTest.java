package com.se491.app.two2er.GetUsers;

import com.se491.app.two2er.GetUsers.GetUsers;
import com.se491.app.two2er.HelperObjects.UserObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by pazra on 5/13/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetUsersTest {

    @Test
    public void TestRun() {
        DistanceRefreshStrategy strat = mock(DistanceRefreshStrategy.class);
        GetUsers gu = new GetUsers(strat);
        gu.run();
    }

    @Test
    public void GetUsersCollectionTest() {
        DistanceRefreshStrategy strat = mock(DistanceRefreshStrategy.class);
        GetUsers gu = new GetUsers(strat);
        gu.run();
        HashMap<String, UserObject> users = gu.getUsersList();
        assertNotNull(users);
    }
}
