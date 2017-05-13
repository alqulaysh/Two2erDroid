package com.se491.app.two2er.GetUsers;

import com.se491.app.two2er.HelperObjects.UserObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by pazra on 5/13/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class RefreshUsersBySubjectFilterTest {

    @Test
    public void constructorTest() {
        RefreshUsersBySubjectFilter strat = new RefreshUsersBySubjectFilter(new HashMap<String, UserObject>(), "");
        assertNotNull(strat);
    }

    @Test
    public void runTest() {
        RefreshUsersBySubjectFilter strat = mock(RefreshUsersBySubjectFilter.class);
        strat.run();
        assertNotNull(strat.getUsersList());
    }
}
