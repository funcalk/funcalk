funcalk
=======
**Fun**ctional **Cal**culus for **K**otlin is a library for defining and calculating
number and higher-order functions. 

Expression BNF
--------------
    <expression> ::= <term> |
                     <expression> + <term> |
                     <expression> - <term>
    <term>       ::= <factor> |
                     <term> * <factor> |
                     <term> / <factor>
    <factor>     ::= power> |
                    - <power> |
                    + <power>
    <power>      ::= <primary> |
                     <primary> ^ <primary>
    <primary>    ::= <number> |
                     <symbol> |
                     (<expression>) |
                     <fun-call>   
    <fun-call>   ::= <symbol>(<expression>)   
    <number>     ::= [0-9]+(.[0-9])?
    <symbol>     ::= [a-zA-Z_0-9]+

REPL BNF
--------------
    <statement>  ::= <expression> |
                     <assignment>
    <assignment> ::= <symbol> = <expression>
