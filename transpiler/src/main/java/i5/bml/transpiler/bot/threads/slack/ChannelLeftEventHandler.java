package i5.bml.transpiler.bot.threads.slack;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.handler.BoltEventHandler;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.ChannelLeftEvent;
import i5.bml.transpiler.bot.events.EventSource;
import i5.bml.transpiler.bot.events.messenger.MessageEvent;
import i5.bml.transpiler.bot.events.messenger.MessageEventType;
import i5.bml.transpiler.bot.events.messenger.slack.SlackUser;

import java.io.IOException;

public class ChannelLeftEventHandler extends AbstractSlackHandler implements BoltEventHandler<ChannelLeftEvent> {

    public ChannelLeftEventHandler(SlackBotThread slackBotThread) {
        super(slackBotThread);
    }

    @Override
    public Response apply(EventsApiPayload<ChannelLeftEvent> event, EventContext context) throws IOException, SlackApiException {
        var slackEvent = new MessageEvent(EventSource.SLACK, event.getEventTime());
        slackBotThread.getActiveSessions().remove(event.getEvent().getChannel());
        slackEvent.setMessageEventType(MessageEventType.BOT_REMOVED);
        slackEvent.setUsername(fetchDisplayName(context.client(), slackBotThread.getBotToken(), event.getEvent().getActorId()));

        slackEvent.setUser(new SlackUser(slackBotThread.getClient(), slackBotThread.getBotToken(), event.getEvent().getChannel()));

        slackBotThread.getEventQueue().put(slackEvent);
        return context.ack();
    }
}