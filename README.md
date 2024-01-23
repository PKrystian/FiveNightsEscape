# FiveNightsEscape:

### The basics
- The application ( game ) operates on the premise of surviving as long as possible while constantly trying to escape appearing monsters on the map

- The player gains experience points the longer they survive on the map

### Difficulty
- Difficulty choice in the main menu determines how often more difficult monsters to deal with will spawn and try to attack the player

### Application

- The game operates with the use of **Google Maps API** which helps track the position of the user and constantly update any movement that the player has performed alerting monsters nearby of their position

- The user has **3 hearts** at the start of the game and has to stay alive throughout the game

- The user has a timer that tracks down how long they have survived within the game, along with the level they have managed to score on the **main UI**

- Monsters spawn based on a random chance every fixed amount of time, filling the immediate area around the player and trying to hunt him down

- There are three types of monsters:

    - **Standing Monster** - This monster has a visual range circle around him and if the player comes in contact with the circle, the monster attacks

    - **Chasing Monster** - This monster has the ability to chase the player down and follow them until they go out of range of their alert distance ( this distance is hidden ), they will try to hunt the player down and attack them

    - **Wandering Monster** - This monster wanders randomly around the map between fixed points and tries to detect a player nearby. Once the player has been detected this monster also has the ability to move towards the player trying to attack them

### Game Over

- Once the player runs out of hearts they **die**, going back to the start menu and **losing all progress** towards their try

# Repository contribution:

## Branch Usage:

As pushing to the master branch is restricted, create feature-specific branches for each task or contribution.
Branch names should reflect the task or feature being implemented for easy tracking and referencing during pull requests.

## Pull Request Workflow:

Every pull request requires at least one approval from a team member.
Provide a clear and concise description in your pull request outlining the changes made, their purpose, and any relevant details, and if you're working on something that changes views/layout etc. add screenshot of the changes.

## Coding Style:

All code contributions must align with the Kotlin style guide ([We use Kotlin style guide](https://developer.android.com/kotlin/style-guide)).
Adherence to consistent formatting, naming conventions, and best practices outlined in the style guide is crucial.

## Other:

Utilize descriptive commit messages to ensure clarity and ease of understanding for the team members reviewing the changes.
Where applicable, leverage Git's features such as rebase or squash commits to maintain a clean and coherent commit history.