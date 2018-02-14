import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PongListener implements IListener<MessageReceivedEvent> {

    private final IDiscordClient client;
    private Map<String, LocalDateTime> lastPingMap;
    private List<String> pingWarned;

    public PongListener(IDiscordClient client) {
        this.client = client;
        lastPingMap = new HashMap<>();
        pingWarned = new ArrayList<>();
    }

    @Override
    public void handle(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        System.out.println(message);
        List<IUser> mentions = message.getMentions();
        if (!mentions.isEmpty()) {
            System.out.println("Message received with mention");
            if (mentions.stream().anyMatch(iUser -> iUser.getDiscriminator().equals("4710"))) {
                System.out.println("Message received with bot mention");
                if (message.getContent().contains("help")) {
                    String messageToSend = "I can do a few things! \n Ping: See if I'm here \n @me who is @target: I'll tell you who @target is";
                    new MessageBuilder(this.client).withChannel(message.getChannel()).withContent(messageToSend).build();

                }
                if (message.getContent().contains("just do it")) {
                    new MessageBuilder(this.client).withChannel(message.getChannel()).withContent("https://m.popkey.co/ca8f87/AoYjv_f-maxage-0.gif").build();
                }
                //two mentions one for bot, one for a user to act on
                if (mentions.size() == 2) {
                    if (message.getContent().contains("who is")) {
                        System.out.println("Who is command executing");
                        IUser otherUser = mentions.stream().filter(iUser -> !iUser.getDiscriminator().equals("4710")).findFirst().get();
                        List<IRole> cleanedRoles = otherUser.getRolesForGuild(event.getGuild()).stream().filter(iRole -> !iRole.isEveryoneRole()).filter(iRole -> !iRole.getName().equalsIgnoreCase("Twitch Subscriber")).collect(Collectors.toList());
                        String suggestedName = otherUser.getNicknameForGuild(message.getGuild()) != null ? otherUser.getNicknameForGuild(message.getGuild()) : otherUser.getName();
                        String messageToSend = suggestedName + " is " + cleanedRoles.get(0).getName();
                        new MessageBuilder(this.client).withChannel(message.getChannel()).withContent(messageToSend).build();
                    } else if (message.getContent().contains("fix")) {
                        if (message.getAuthor().getRolesForGuild(message.getGuild()).stream().anyMatch(iRole -> iRole.getPermissions().contains(Permissions.ADMINISTRATOR))) {
                            IUser otherUser = mentions.stream().filter(iUser -> !iUser.getDiscriminator().equals("4710")).findFirst().get();
                            List<IRole> cleanedRoles = otherUser.getRolesForGuild(event.getGuild()).stream().filter(iRole -> !iRole.isEveryoneRole()).filter(iRole -> !iRole.getName().equalsIgnoreCase("Twitch Subscriber")).collect(Collectors.toList());
                            String suggestedName = otherUser.getNicknameForGuild(message.getGuild()) != null ? otherUser.getNicknameForGuild(message.getGuild()) : otherUser.getName();
                            String messageToSend = suggestedName + " is now " + cleanedRoles.get(0).getName();
                            message.getGuild().setUserNickname(otherUser, "test");
                            new MessageBuilder(this.client).withChannel(message.getChannel()).withContent(messageToSend).build();
                        } else {
                            new MessageBuilder(this.client).withChannel(message.getChannel()).withContent("Sorry only admins can fix things you pleb").build();
                        }

                    }
                }
            }
        }
        if (message.getContent().equalsIgnoreCase("ping")) {
            if (lastPingMap.containsKey(message.getAuthor().getDiscriminator())) {
                if (lastPingMap.get(message.getAuthor().getDiscriminator()).plusMinutes(1).isBefore(LocalDateTime.now())) {
                    new MessageBuilder(this.client).withChannel(message.getChannel()).withContent("PONG").build();
                    pingWarned.remove(message.getAuthor().getDiscriminator());
                } else {
                    if (!pingWarned.contains(message.getAuthor().getDiscriminator())) {
                        new MessageBuilder(this.client).withChannel(message.getChannel()).withContent("NO PING FOR YOU. YOU ARE IN TIMEOUT").build();
                        pingWarned.add(message.getAuthor().getDiscriminator());
                    }
                }
            } else {
                new MessageBuilder(this.client).withChannel(message.getChannel()).withContent("PONG").build();
            }
            lastPingMap.put(message.getAuthor().getDiscriminator(), LocalDateTime.now());
        }

    }
}

