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

#### Screenshot

<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/7e354292-23a3-4154-a11b-e2804a36acc2" width="128"/> 
<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/7b724109-b858-4ff2-8ade-8baf0fbe6d41" width="128"/> 

<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/f15032df-a150-404c-b8d3-198d36302c8b" width="128"/> 
<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/01773b60-2b0f-4d09-8233-7abf45dc6a50" width="128"/> 

<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/330fb889-5abb-4f93-b3cc-3d34a691b746" width="128"/> 
<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/67dcc051-d2bb-4fff-8f8d-8c0566206583" width="128"/> 

<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/d941c7e7-57fa-4e84-a699-36c127ffc3c7" width="128"/> 
<img src="https://github.com/ngoctuyendang/AndroidMaps/assets/49066660/aee3613a-b163-4f8b-9d1e-8499e2cfe7c6" width="128"/>

