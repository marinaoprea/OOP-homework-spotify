<p>
Current source proposes a solution for spotify's main actions implementation.
</p>

<h4>Classes:</h4>
<ul> 
<li>
    Database: <br>
    <ul>
        <li> contains shallow copy to the lists of input songs and input podcasts </li>
        <li> contains an array list of all the playlists created, both private and public;
           reallocations number is diminished by changing privacy status instead of adding
           and removing elements from the list </li>
        <li> contains an array list of all the users with their afferent information </li>
        <li>contains global list
            of normal users, global list of artists and global list of 
            hosts (used for printing all users in specific order: normal
            users, artists, hosts</li>
            <li>contains global list of albums</li>
    </ul>
</li>
<li>
    Playlist: <br>
    <ul>
        <li> has fields for all the information regarding a playlist; contains an array list
           of input songs</li>
    </ul>
</li>
<li>
    User:<br>
    <ul>
        <li>contains information about the activity of the user</li>
        <li>contains static inner class SavedHistory that helps reload a podcast from where
           it remained; class is static as we only use the constructor, which is not specific
            to the user; class is inner because it makes sense only by user activity</li>
        <li>contains a list of playlists created by the user, a list of followed playlists
           and a playlist of favourite songs</li>
    </ul>
</li>

<li>
    Command:<br>
    <ul>
        <li>generic class made to be inherited </li>
        <li>has not been made abstract in order for command to be upcasted and use the generic
           constructor, generic conversion to ObjectNode and error messages to be
           reprinted in case of execution method not being overrode </li>
    </ul>
</li>

<li>
    Constants: <br>
    <ul>
        <li> contains constants used in implementation </li>
    </ul>
</li>
<li>Artist:
        <ul>
            <li>extends user class, which offers the artist the possibility
to also behave like a normal user (that is listen to audio files, create playlists, etc)</li>
            <li>contains a collection of released albums</li>
            <li>contains static inner classes Event and Merch; classes are
            static as we only use the constructor and methods, which are not
            specific to the artist; classes are inner because they make sense only
            regarding artist activity</li>
            <li>contains a collection of published events and released merch</li>
        </ul>
    </li>
    <li>Host:
        <ul>
            <li>similar to artist class, extends user class</li>
            <li>contains collection of released podcasts</li>
            <li>contains static inner class Announcement</li>
            <li>contains collection of published announcements</li>
        </ul>
    </li>
    <li>Album:
        <ul>
            <li>collection of songs created by an artist</li>
        </ul>
    </li>
    </ul>

<h4>Wrappers</h4>
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
    
<h4>Command workflow</h4>
<p>
Every command is implemented in the execute method of a corresponding class; both command input
and command output are structured as fields inside the class. Commands are sequentially parsed
and execution method of specific command is called. Then, outputs are sequentially added to
the final output array.</p>
<p><b><i>Command factory</i></b> and <b><i>command
invoker</i></b> have been introduced. Command factory creates a command's object with
its dynamic type corresponding to the command input requested type. Command
invoker then calls the execute method overrode in every command class, resulting in
a behaviour that bases on the dynamic type of the command.</p>
<p>
<i>End Program</i> command has been added, that is a command which provides final
statistics about artists (gained revenue, ranking etc.). Command is introduced with the timestamp of the last command in
program run.</p>
    
<h4>Time simulation</h4>
The information we need to track in order to correctly simulate a user's activity is:
<ul>
    <li>type of the selected audio file (selectedType)</li>
    <li>user's last search's results, from which he will later select</li>
    <li>selected index in this list indicating the name of the audio file loaded (SelectedIndex)</li>
    <li>in case of playlists and podcasts, another index to indicate the song/episode selected (thus
       selectedIndexInList is activated)</li>
    <li>cursor in the audio file (timeRelativeToSong)</li>
    <li>the timestamp of last update/simulation performed</li>
</ul>
<p>
    The two indexes act like pointers and are incremented/decremented to update the information
according to the time that has passed, repeat mode and shuffle mode the user has chosen. timeRelativeToSong 
acts like a cursor inside a song/podcast episode, giving the information
needed when we calculate if current audio file has ended.
</p>
    <p>
    In User class we have simulate method implemented that simulates the user's app behaviour up until
    given moment. Thus, we can in a discrete way (calling this method at certain moments) simulate a
    continuous behaviour. Every command that changes the simulating behaviour calls for this method (load,
    select, next, prev, forward, backward, playPause) and also every command that needs an up-to-date
    status calls for this method (like, addRemoveInPlaylist, follow, etc.).
    </p>

<h4>Implementation issues:</h4>
<ul>
    <li>input information was not deep copied and was not extended through inheritance </li>
    <li>thus information sometimes is not very well organised and thus searchBar, the top command
        in the command sequence uses as parameters both input library and extended database.</li>
    <li>database contains a list of input songs, whom class was not reconstructed; thus for implementing
        GetTop5Songs, a vector of likes for each song was aggregated in Database class; for maintaining index
        correspondence, an insertion sort approach was handled; time complexity is in O(n), compared to an
        n * log n for sort, but code clarity is lessened.</li>
</ul>

<h4>Search bar </h4>
    Search in database for album, artist and host have been introduced. Results
are still obtained as strings. Their interpretation will be different in case
of selection, user's field selectedType can now have following values:
{"song", "playlist", "podcast", "album", "artist", "host"}.

<h4>Adding audio files</h4>
    By adding a new album, we also add all songs contained in the global collection
of songs. Podcasts are simply added in the global list of podcasts.

<h4>Removing audio files</h4>
    Firstly simulation is performed up until removal point and then check method
is called to ensure that no user is interacting with the given file.
    Removing audio files(albums or podcasts) implies removing them from their
specific global lists and removing them from users' "saved collections". That
is, songs are removed from all playlists they were being part of and podcasts
are removed from user's saved history list.

<h4>Adding users</h4>
    Adding users implies their objects being constructed based on the requested
type and then users being introduced in database's global list of users and
in database's lists based on user's type.

<h4>Deleting users</h4>
    When deleting a normal user, we first check if any other users interacts
with the playlists created by him. If not, we delete all user's playlists, decrease
likes gained by the songs in user's favourites playlist and decrease the 
followers number of playlist followed by the user.
    As Artist and Host extend User class, check and clear methods are overrode.
However, the overrode methods are called as we firstly check if artist/host could
be deleted as normal users and we also delete the playlists that they may have created.
    Furthermore, for artist we check if any of his albums interacts with the other
users. If not, we clear all albums created by artist and delete artist from 
global lists in database. Similar for hosts.

<h3>Page System</h3>
    <h4>Page Hierarchy</h4>
        All types of pages extend generic Page class that contains reference
to its owner; Page class contains accept method that accepts page visitor.
    <h4>Page Printer</h4>
        For page printing, visitor pattern was implemented. That is PagePrinter
class implements PageVisitor and provides overloaded visit method that returns
string formatted according to page's dynamic type. All pages implement VisitablePage
interface, implementing the accept method that accepts page visitor.
        Pattern separates printing algorithm from page classes and permits
printing operations to be added without internal structure of pages to be modified.
Furthermore, in case of different types of visitors being introduced, pages should only
overload accept method (minimal change in their internal structure).
    <h4>Users' interaction with pages</h4>
    When user is created, a new home page and liked page are created for him. When
an artist/host is created, artist page/ host page is created for him.
    Users have reference to their home page, liked content page and current
page. When user changes pages, current page reference changes to the new page accessed.
When user selects an artist/host, current page reference changes to artist's or host's
page.

<h3>Collisions</h3>
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

