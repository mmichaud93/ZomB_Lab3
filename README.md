ZomB
=====


## What?

This project is a language called ZomB that was designed and coded for Lab 3 in Programming Languages COMP 501.

The task was to design a language in a domain that we were allowed to choose, write the BNF, EBNF, and the Lexer/Parser.

ZomB is a language that defines and give progromatic meaning to zombie encounter literature. So the phrase `Human sees zombie.` will have meaning in the 
respect of `<entity> <verb> <entity>`.  

To do this __the language had to support every engish word__, not just what we package with it, because literature has support for every word. To do that we 
wrote the code to accept casting to give meaning, for example the word `Matt` isnt in the predefined lexeme so to add it you write `Matt(entity)` and
that will allow `Matt` to be referenced in the code and have meaning.

`Matt(entity) sees a zombie.
Matt wrecked(verb) the zombie.
Zombie dies.`