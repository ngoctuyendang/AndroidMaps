# README
#### Task List
###### 1. User can see my current location on a map
  - The first step, check GPS service is turn on?
    - If is turned on -> next step
    - else show popup to user turn on
  

  - The next step, check position permission
    - If permission is allowed -> next step
    - else show maps without current user's position


  - The next step, show maps with current user's position

###### 2. User can save a short note at my current location
- Connect project to Firebase to save note
  - Click on snippet of marker to add note. The new note contains name, text, lat, long.

###### 3. User can see notes that user or other users added (task 3 and 4 in backlog)
- Get all notes from firebase
  - If note at current user's position -> add all note to snippet of current marker
  - else add new marker in this position and add all note to snippet

###### 4. User has the ability to search for a note based on contained text or user-name
- Get all notes from firebase, browse each element in the list, if the user name or text contains keyword -> add to a search result list.
- Move to the position when click on item is list -> TODO: current move to the position, do not auto show note 
- _This task should be combined with the backend to get results when having big data, browsing the big list makes low performance or error._