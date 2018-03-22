# BBManager
Blood Bowl 2 command line editor for offline (hotseat) league tracking

If you are a fan of [Blood Bowl](http://www.bloodbowl-game.com/) and purchased either Blood Bowl 2 or Blood Bowl 2: Legendary Edition on PC, you might be - as many others - extremely disappointed at the lack of an offline (hotseat) league option.

This command line tool provides capability to edit - **AT YOUR OWN RISK** - the SQLite database where team and player information is stored so that, with a bit of paper tracking, you can update the stats after a friendly match.

**This tool is not a cheat/trainer!**, it only allows you to edit **offline** team data, much like the "Custom team" feature of the Legendary Edition, except that feature is only usable at team creation.
This tool is based on the rules found in the [Blood Bowl Living Rulebook version 6](http://bloodbowlgamers.com/downloads/LRB6.pdf) and might NOT reflect the actual rules implemented in the game - yet, since I saw some rule tables in the DB, and need to investigate further there..

**This tool is an alpha version!**, it works without hiccups - but its a barebone tool with some open todos and limitations and the user experience can be further improved, but **it works** as a starting point if you, like me, crave this feature! :)

## Usage
**Get paper and pen during a game** and record the following events for each player:
* **successful pass**: if a player makes a pass (except hand-off) and the receiver catches it successfully -> +1 SPP
* **injury**: if a player injures or kills another player (NOT stun, NOT KO results) -> +2 SPP
* **interception**: if a player successfully intercepts a pass -> +2 SPP
* **touchdown**: +3 SPP
* **MVP**: at the end of each game one player per team is selected as MVP automatically -> +5 SPP
* **dead**: if a player is dead, mark it down
* **injuries**: if a player suffers a long term injury, eg: -1 point to an attribute, mark it down

Just consider the result even if a reroll or apothecary has been used.

To use the tool, download the [bbmanager.jar](bbmanager/out/artifacts/bbmanager_jar/bbmanager.jar) JAR file uploaded here or check out the whole project and build it yourself. You need at least **Java 8** on your system to run the tool and the JDK of course if you wish to improve it.

The **Management.db** SQLite database for your game on Windows should be located in *C:\Users\your_user_name\My Documents\BloodBowl2\Profiles\some_random_name\ManagementLocal* - **ALWAYS MAKE A BACKUP COPY BEFORE EDITING IT**

Copy the *Management.db* file in the same folder where you placed the *bbmanager.jar* jar and open a command line there. Then you can run the tool as:

`java -jar bbmanager.jar`

With the following options available:
* **--debug**: to enable verbose tracing of operations
* **--skip-db-bak**: to avoid automatically creating a backup copy of your *Management.db* file named *Management.db.bak* in the same folder as the *bbmanager.jar*. The backup process will **ALWAYS OVERWRITE** the current backup file.

Then you can access the following operations by pressing the indicated key and then hitting ENTER:
* **p** or **P**: update a player information
* **t** or **T**: update a team information
* **q** or **Q**: quit the tool
* **h** or **H**: show the usage help

### Player information
In this menu you can edit a player by first entering its name, including spaces if any. At the moment it is **case sensitive**!
Also, duplicate entries would result in an error, so try to NOT use the same names for your players!

Then you can insert the following values:
* **MA**: movement allowance, insert a positive or negative value to modify the current value of the same amount. Minimum value after the operation is 1 
* **ST**: strength, insert a positive or negative value to modify the current value of the same amount. Minimum value after the operation is 1 
* **AG**: agility, insert a positive or negative value to modify the current value of the same amount. Minimum value after the operation is 1 
* **AV**: armor value, insert a positive or negative value to modify the current value of the same amount. Minimum value after the operation is 1 
* **EXP**: experience, insert a positive value to increase the current player experience (SPP) of the same amount. The level gains are automatically calculated and will be shown in game the next time you start it. Maximum player level is 7 that corresponds to 176 SPP.
* **DEAD**: insert 1 if this player died.

If you do not want to set a value, insert **x** or **X**

### Team information
In this menu you can edit a team by first entering its name, including spaces if any. At the moment it is **case sensitive**!
Also, duplicate entries would result in an error, so try to NOT use the same names for your players!

Then you can specify whether the team won (**w** or **W**), tied (**x** or **X**) or lost (**l** or **L**) the game to update the cash and popularity accordingly.

If the team won, the rules allow for a die reroll. This roll is used to determine the cash gain after the match and can be rerolled only **once** and the second result **must be kept**, even if worse.

### Updated data import
After you are finished with all your modifications, replace the *Management.db* file with the one you just modified and start the game. Your changes should be reflected on your players and teams.

## Third party libraries
This tool includes the SQLite-JDBC jar from https://github.com/xerial/sqlite-jdbc which is distributed under the [Apache Commons 2.0 license](http://www.apache.org/licenses/LICENSE-2.0)
