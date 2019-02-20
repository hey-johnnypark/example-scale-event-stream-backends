package hello;

import com.github.johnnypark.webasyncscaling.services.UserSessions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class UserSessionsTest {

  private UserSessions userSessions;

  @Before
  public void setUp(){
    userSessions = new UserSessions();
  }

  @Test
  public void testSessionCreated() {
    Assert.assertThat(userSessions.sessionCreated("foo", "id1"), Matchers.equalTo(1));
    Assert.assertThat(userSessions.sessionCreated("foo1", "id1"), Matchers.equalTo(1));
    Assert.assertThat(userSessions.sessionCreated("foo", "id1"), Matchers.equalTo(1));
    Assert.assertThat(userSessions.sessionCreated("foo1", "id1"), Matchers.equalTo(1));
    Assert.assertThat(userSessions.sessionCreated("foo", "id2"), Matchers.equalTo(2));
    Assert.assertThat(userSessions.sessionCreated("foo1", "id2"), Matchers.equalTo(2));
  }

  @Test
  public void testSessionRemove() {
    userSessions.sessionCreated("foo", "id1");
    userSessions.sessionCreated("foo", "id2");
    userSessions.sessionCreated("foo1", "id1");
    Assert.assertThat(userSessions.sessionDropped("foo", "id2"), Matchers.equalTo(1));
    Assert.assertThat(userSessions.sessionDropped("foo", "id1"), Matchers.equalTo(0));
  }

  @Test
  public void hasSessions() {
    Assert.assertThat(userSessions.hasSessions("foo"), Matchers.is(false));
    userSessions.sessionCreated("foo", "id1");
    Assert.assertThat(userSessions.hasSessions("foo"), Matchers.is(true));
    userSessions.sessionDropped("foo", "id1");
    Assert.assertThat(userSessions.hasSessions("foo"), Matchers.is(false));
  }



}
