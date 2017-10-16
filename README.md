# quidditchtimekeeper
A timekeeper for Quidditch

Features:
- provides a global game time
- custom team names
- custom team jerseys (16 different colors)
- keeps the score
- add penalties (blue, yellow, red cards)
  - jersey number of player
  - name of player
  - every penalty gets their own watch which is linked to game time (pause only game time to stop all watches)
- regulation time, first overtime, second overtime
- reminders for all Quidditch events (release player, release snitch, get seekers ready, etc.)

Features in the future:
- remove donation dialog
- store games in the data base for more robust implementation (data loss due to shortage of RAM)
- display scores according to http://bit.ly/2hGvV7E
- add possibility for reasoning of a penalty ("Contact from behind", "Contact around the neck", etc.)
- create an API to submit game information to a server (e.g, JSON string)
- create an interface to download game information from a server
- add possibility to design your own jersey online and download it to the app
- create a data base of teams in Europe on quidditchtimekeeper.org with their
  - name
  - origin
  - player information
    - jersey number
    - name
    - position
  - coat of arms

Licensed under the [GNU GENERAL PUBLIC LICENSE](LICENSE).
