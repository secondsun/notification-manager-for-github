/*
 * Copyright (C) 2016 Your Organisation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package net.saga.github.notifications.test.deserialize;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import net.saga.github.notifications.manager.vo.Notification;
import net.saga.github.notifications.service.persistence.ObjectMapperFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 *
 * @author summers
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestDeserialize {

    private static final String SERIALIZED_NOTIFICATION = " {\n" +
"    \"id\": 151832852,\n" +
"    \"repository\": {\n" +
"      \"id\": 7779922,\n" +
"      \"owner\": {\n" +
"        \"id\": 2572945,\n" +
"        \"login\": \"fheng\",\n" +
"        \"avatar_url\": \"https:\\/\\/avatars.githubusercontent.com\\/u\\/2572945?v=3\",\n" +
"        \"gravatar_id\": \"\",\n" +
"        \"url\": \"https:\\/\\/api.github.com\\/users\\/fheng\",\n" +
"        \"html_url\": \"https:\\/\\/github.com\\/fheng\",\n" +
"        \"followers_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/followers\",\n" +
"        \"following_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/following{\\/other_user}\",\n" +
"        \"gists_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/gists{\\/gist_id}\",\n" +
"        \"starred_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/starred{\\/owner}{\\/repo}\",\n" +
"        \"subscriptions_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/subscriptions\",\n" +
"        \"organizations_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/orgs\",\n" +
"        \"repos_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/repos\",\n" +
"        \"events_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/events{\\/privacy}\",\n" +
"        \"received_events_url\": \"https:\\/\\/api.github.com\\/users\\/fheng\\/received_events\",\n" +
"        \"type\": \"Organization\",\n" +
"        \"site_admin\": false\n" +
"      },\n" +
"      \"name\": \"fhcap\",\n" +
"      \"full_name\": \"fheng\\/fhcap\",\n" +
"      \"description\": \"FeedHenry's Cloud Application Platform\",\n" +
"      \"fork\": false,\n" +
"      \"url\": \"https:\\/\\/api.github.com\\/repos\\/fheng\\/fhcap\",\n" +
"      \"html_url\": \"https:\\/\\/github.com\\/fheng\\/fhcap\",\n" +
"      \"private\": true\n" +
"    },\n" +
"    \"subject\": {\n" +
"      \"title\": \"Bumping components for RHMAP-7679-http-gitlab-shell\",\n" +
"      \"url\": \"https:\\/\\/api.github.com\\/repos\\/fheng\\/fhcap\\/pulls\\/2511\",\n" +
"      \"latest_comment_url\": \"https:\\/\\/api.github.com\\/repos\\/fheng\\/fhcap\\/issues\\/comments\\/229966340\",\n" +
"      \"type\": \"PullRequest\"\n" +
"    },\n" +
"    \"reason\": \"subscribed\",\n" +
"    \"unread\": true,\n" +
"    \"updated_at\": 1467384456,\n" +
"    \"last_read_at\": null,\n" +
"    \"userId\": \"secondsun\",\n" +
"    \"url\": \"https:\\/\\/api.github.com\\/notifications\\/threads\\/151832852\"\n" +
"  }";
//

    
    @Test
    public void testZoneDeserializeJackson() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
        Notification notification = mapper.readValue(SERIALIZED_NOTIFICATION, Notification.class);
        Assert.assertNotNull(notification);
    }

}
