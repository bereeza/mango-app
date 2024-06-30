|HTTP Method|Path|Action|
|-|-|-|
| GET | / | return String msg 'PLease, wigIn or signUp'|
| POST | / | save the user in DB |
| GET | /sigin | if the user excist - he passes check  |
| GET | /home | get all posts from all users |
| GET | /home/{id} | get post by id or else throw exception |
| POST | /home/{id}/c | add a comment to an existing post |
| GET | /u/{id} | get all personal user posts |
| POST | /u/{id} | add new post |
| DELETE | /u/{id}/post/{postId} | delete concrete user post |
| PUT | /u/{id}/update/{postId} | update concrete user post |
