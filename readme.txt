Compile all java files using command: javac *.java
Run driver program using command: java foo <sourceId> <duration> <destinationId> <message> <neighbor(s)>
Run at most two processes on one machine.

For example:
in Scenario 1, there are 2 processes, we can use one or two machines:
Run node0 on csgrads1 using command: java foo 0 100 1 "EFABCXXFEGHI" 1 
Run node1 on dc01 using command: java foo 1 100 1 0 
Or run both nodes on same machine (csgrads1 or dc01) using command:
java foo 0 100 1 "EFABCXXFEGHI" 1 &
java foo 1 100 1 0 &

in Scenario 4, since there are 6 processes, we need 3 machines: csgrads1, dc01, dc02
Run node0 and node1 on csgrads1:
java foo 0 200 5 "MG0" 1 2 &
java foo 1 200 1 0 2 &
Run node2 and node3 on dc01:
java foo 2 200 2 0 1 3 4 &
java foo 3 200 3 2 4 &
Run node4 and node5 on dc02
java foo 4 200 4 2 3 5 &
java foo 5 200 0 "MG5" 4 &

**Note:
Each time before you start a new scenario, make sure you delete all the previous output files.
(Any files like nodeXreceived or fromXtoY)

Commands used to run each Scenario (run one or two processes on one machine):
1:
java foo 0 100 1 "EFABCXXFEGHI" 1 &
java foo 1 100 1 0 &
2:
java foo 0 100 1 "EFABCXXFEGHI" 1 &
3:
java foo 0 100 2 "EFABCXXFEGHI" 1 &
java foo 1 100 1 2 &
4:
java foo 0 200 5 "MG0" 1 2 &
java foo 1 200 1 0 2 &
java foo 2 200 2 0 1 3 4 &
java foo 3 200 3 2 4 &
java foo 4 200 4 2 3 5 &
java foo 5 200 0 "MG5" 4 &
5:
java foo 0 200 1 "01234567890abcde" 1 &
java foo 1 200 1 0 &
6
java foo 0 100 0 1 2 3 &
java foo 1 100 0 "From1" 0 &
java foo 2 100 0 "From2" 0 &
java foo 3 100 0 "From3" 0 &