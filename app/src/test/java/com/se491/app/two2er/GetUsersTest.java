package com.se491.app.two2er;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by pazra on 4/2/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class GetUsersTest {

    @Test
    public void Test()
    {
        GetUsers gu = mock(GetUsers.class);
        gu.doInBackground();
    }
}
