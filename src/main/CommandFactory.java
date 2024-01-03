package main;

import commands.*;
import commands.additions.*;
import commands.removals.*;
import commands.show.ShowAlbums;
import commands.show.ShowPlaylists;
import commands.show.ShowPodcasts;
import commands.show.ShowPreferredSongs;
import commands.statistics.*;

public final class CommandFactory {
    /**
     * constructs new command based on command input type
     * @param commandInput input command we are constructing
     * @return new command
     */
    public Command getCommand(final CommandInput commandInput) {
        return switch (commandInput.getCommand()) {
            case "search" -> new SearchBar(commandInput);
            case "select" -> new Select(commandInput);
            case "load" -> new Load(commandInput);
            case "playPause" -> new PlayPause(commandInput);
            case "status" -> new Status(commandInput);
            case "createPlaylist" -> new CreatePlaylist(commandInput);
            case "addRemoveInPlaylist" -> new AddRemoveInPlaylist(commandInput);
            case "like" -> new Like(commandInput);
            case "showPlaylists" -> new ShowPlaylists(commandInput);
            case "showPreferredSongs" -> new ShowPreferredSongs(commandInput);
            case "repeat" -> new Repeat(commandInput);
            case "shuffle" -> new Shuffle(commandInput);
            case "next" -> new Next(commandInput);
            case "prev" -> new Prev(commandInput);
            case "forward" -> new Forward(commandInput);
            case "backward" -> new Backward(commandInput);
            case "follow" -> new Follow(commandInput);
            case "switchVisibility" -> new SwitchVisibility(commandInput);
            case "getTop5Songs" -> new GetTop5Songs(commandInput);
            case "getTop5Playlists" -> new GetTop5Playlists(commandInput);
            case "switchConnectionStatus" -> new SwitchConnectionStatus(commandInput);
            case "getOnlineUsers" -> new GetOnlineUsers(commandInput);
            case "addUser" -> new AddUser(commandInput);
            case "addAlbum" -> new AddAlbum(commandInput);
            case "showAlbums" -> new ShowAlbums(commandInput);
            case "printCurrentPage" -> new PrintCurrentPage(commandInput);
            case "addEvent" -> new AddEvent(commandInput);
            case "addMerch" -> new AddMerch(commandInput);
            case "getAllUsers" -> new GetAllUsers(commandInput);
            case "deleteUser" -> new DeleteUser(commandInput);
            case "addPodcast" -> new AddPodcast(commandInput);
            case "showPodcasts" -> new ShowPodcasts(commandInput);
            case "removePodcast" -> new RemovePodcast(commandInput);
            case "addAnnouncement" -> new AddAnnouncement(commandInput);
            case "removeAnnouncement" -> new RemoveAnnouncement(commandInput);
            case "removeAlbum" -> new RemoveAlbum(commandInput);
            case "changePage" -> new ChangePage(commandInput);
            case "removeEvent" -> new RemoveEvent(commandInput);
            case "getTop5Albums" -> new GetTop5Albums(commandInput);
            case "getTop5Artists" -> new GetTop5Artists(commandInput);
            case "wrapped" -> new WrapperCommand(commandInput);
            case "subscribe" -> new Subscribe(commandInput);
            case "getNotifications" -> new GetNotifications(commandInput);
            case "endProgram" -> new EndProgram(commandInput);
            case "buyMerch" -> new BuyMerch(commandInput);
            case "seeMerch" -> new SeeMerch(commandInput);
            case "buyPremium" -> new BuyPremium(commandInput);
            case "cancelPremium" -> new CancelPremium(commandInput);
            case "previousPage" -> new PrevPage(commandInput);
            case "nextPage" -> new NextPage(commandInput);
            case "updateRecommendations" -> new UpdateRecommendations(commandInput);
            case "loadRecommendations" -> new LoadRecommendations(commandInput);
            case "adBreak" -> new AdBreak(commandInput);
            default -> new Command(commandInput);
        };
    }
}
