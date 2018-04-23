java foo 0 200 2 "this is a message from 0" 1
java foo 1 200 2 "this is a message from 1" 0 2
java foo 2 200 2 1

java foo 0 100 1 "EFABCXXFEGHI" 1 
java foo 1 100 1 0 

java foo 0 100 1 "EFABCXXFEGHI" 1

java foo 0 100 2 "EFABCXXFEGHI" 1
java foo 1 100 1 2


java foo 0 200 5 "MG0" 1 2 &
java foo 1 200 1 0 2 &
java foo 2 200 2 0 1 3 4 &
java foo 3 200 3 2 4 &
java foo 4 200 4 2 3 5 &
java foo 5 200 0 "MG5" 4 &

java foo 0 200 1 "01234567890abcde" 1 &
java foo 1 200 1 0 &

java foo 0 100 0 1 2 3 &
java foo 1 100 0 "From1" 0 &
java foo 2 100 0 "From2" 0 &
java foo 3 100 0 "From3" 0 &