<p>Current source proposes an extension of the previous Spotify implementation, 
including statistics for all types of users, monetization for artists and a page 
navigation system.</p>

<h3>
Classes
</h3>

<h5>Wrappers</h5>
<p>There are three different but similar types of wrappers which contain statistics 
information stored in hashmaps.</p>

<ul>
    <li> ObserverWrapper interface
        <ul>
            <li>interface implemented by all types of wrappers</li>
            <li>provides update methods that are called whenever statistics should be updated</li>
            <li>all methods are default so that every wrapper can implement only its specific statistics</li>
        </ul>
    </li>
    <li> Wrapper
        <ul>
            <li>contains statistics for the normal user, stored as entries in hashmaps of either <i>String, Integer</i> or <i>Wrappeable, Integer</i> </li>
            <li>the value in every entry of the hashmaps represents number of listens of that specific item</li>
            <li>implements update methods for all required statistics for normal user, introducing new  items in the statistics
                or updating the number of listens if they were already taken into account</li>
        </ul>
    </li>
    <li> WrapperArtist, WrapperHost
        <ul>
            <li>classes very similar to Wrapper class for normal users, but containing only specific statistics needed
            for artists/hosts</li>
        </ul>
    </li>
    <li>Wrappeable interface
        <ul>
            <li>generalization of wrapped item, which we store in hashmaps as keys and sort based on listens as values</li>
            <li>used for generic results extraction in Wrapped Command</li>
            <li>provides extractName method</li>
        </ul>
    </li>
</ul>

<h3>Command workflow</h3>
<p>Commands are still sequentially parsed, but <i>End Program</i> command has been added, that is a command which provides final
statistics about artists (gained revenue, ranking etc.). Command is introduced with the timestamp of the last command in
program run.</p>

<h3>Database</h3>
<p>In case of songs and albums, name collisions have occurred. Thus, ids were introduced as primary keys. This way, search results were preserved as 
Strings, but due to ids' memorization they could be correctly then identified. Database class contains now new fields of
<i>songId</i> and <i>albumId</i> that are continuously increased when new items are added. Fields are not static as they could differ between
databases.

At load time of input library, all artists given as names in songs' artist field were introduced as objects in database in order
for statistics on artists to generally work. However, in case of name collision with artists added by AddUser command, they were
overriden in order for later AddAlbum construction to be correctly and unambiguously performed.
</p>

<h3>Statistics</h3>
<p>
    History and activity of normal users have been memorized as hashmaps. Thus, <i>hashCode</i> and <i>equals</i> methods were overriden for items that were
introduced as Wrappeables. For songs, these methods take into consideration only song's name and song's artist. This way, statistics are cumulated in
case of a song appearing in different albums. However, RemoveAlbum command, which also removes all songs contained in that album, was changed in order
for the removed song to be properly identified. Thus, <i>Collections.remove()</i> (which bases on <i>equals</i>) method was replaced by <i>Iterator</i> methods.
</p>
<p> Through hashmaps implementation, statistics are persistent in case of song/album/user removal.</p>

<h3>Monetization</h3>
<p>Events that set off monetization are Ad being played or user cancelling Premium subscription. Thus, as they do not overlap, same
list was used as song history for normal users. However, a copy of song history is performed at BuyPremium command and restored at CancelPremium command.
This way, in case of Ad Break after cancellation, all corresponding songs are monetized.
</p>
<p>
When calculation is performed, songs are organized by artist and then price is divided depending on number of listens.
</p>

<h3>Changes on User</h3>
<p>
    New flag was introduced in order to know the status of the user <i>free/premium</i> for later monetization calculation.
</p>
<p> User's activity is observed by both its inner song history and wrapper object updating statistics whenever user has accessed something new.
Thus all time simulation methods were updated.</p>
<p> Ad flag indicates if ad break should be introduced in user's normal listening flow. Price field stores the price of the to-be-monetized ad.
Ad breaks are included in the simulation method, depending on ad flag, when next track is accessed.</p>

<h3>Design patterns:</h3>
    <ul>
        Previously introduced:
        <li>factory for creating command objects based on type specified at input</li>
        <li>command pattern for executing commands based on command's dynamic type</li>
        <li>visitor for printing different types of page contents (separates printing algorithm from page classes and permits
        printing operations to be added without internal structure of pages to be modified)</li>
    </ul>
    <ul>
        Newly added design patterns:
        <li>observer pattern on notifications:
                <ul>
                    <li>observers: normal users, implement update method (from ObserveContentCreator interface) through which new notification is added in their
                        notification list</li>
                    <li>observables: artists, hosts (content creators), implement notify method (from ObservableUser interface) through each they iterate
                        through their list of observers and notify them whenever their internal structure has changed (that is
                        albums/podcasts being added/removed, merch/events/announcements being added/removed etc.)</li>
                </ul>
        </li>
        <li>factory pattern to generate ChangePage command depending on specific input type</li>
        <li>command pattern on Change Page
            <ul>
                <li>every change page command implements ChangePage interface that provides execution method</li>
                <li>NavigationInvoker class calls for execution method based on ChangePage command dynamic type</li>
                <li>NavigationInvoker class provides inner navigation history and undo history that allows undo and redo
                    commands to be performed</li>
            </ul>
        </li>
        <li>
            The interaction between user and its statistics resembles observer pattern, as wrappers observe any changes in user's activity and user
            notifies wrappers whenever he has accessed something new. Pattern is not vivid as these classes are intertwined.
        </li>
    </ul>

