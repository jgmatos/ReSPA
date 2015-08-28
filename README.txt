This file explains how to use ReSPA.





1) Source Code. A brief description of the main packages of ReSPA is presented bellow:
	
	1.1) input: The input package contains classes used to parse the input of the user and to represent the symbolic variables;
	1.2) leak: The leak package composes all methods used to calculate the leakage of a path condition;
	1.3) log: All classes used for logging operations. Mainly relevant for evaluation and debugging purposes; 
	1.4) main: The ReSPA algorithm
	1.5) output: Classes required to create an alternative input from the path condition.




2) Download and Configure Java Pathfinder

2.1) Our ReSPA prototype was built on top of Java Pathfinder: http://babelfish.arc.nasa.gov/trac/jpf
Therefore one must download the source code of jpf-core (http://babelfish.arc.nasa.gov/hg/jpf/jpf-core) and
the source code of jpf-symb (http://babelfish.arc.nasa.gov/hg/jpf/jpf-symbc). Instructions can be found
on the links provided.

2.2) ReSPA was last tested with the repository versions of jpf-core and jpf-symbc, 905 and 434 respectively, 
therefore it is advisable to use these versions.

2.3) The file StringExpression.java made available in this repository, must replace the one of jpf-symbc.
This is because we added a few modifications to the string representation of StringExpression. 






3) The ReSPA's repository already includes an updated runnable jar. 
However, if you aim at changing the source code, then export ReSPA 
as a runnable jar, using the class respa.main.Main as the main class. 

4) Run ReSPA

4.1) The first step is to create a directory .respa in the user's home. Then, inside .respa, one must create a file
respa.properties and specify some options. An example of such file is available in ReSPA's repository.

4.2) ReSPA assumes that an exception/error occurred in the target project of the user and that a directory
of the incident was created within .respa, e.g. ~/.respa/incidentDir. Inside some mandatory files must be 
available:

	4.2.1) A file exceptionStackTrace.txt
	4.2.2) A file input.txt 
	4.2.3) A file inputLocations.txt
	4.2.4) A file ReSPA.jpf 

Their relevance is described next.


4.2) The jar can be executed in any directory:

	java -jar ReSPA.jar incidentDir target_projectDir

however, we must provide the exact path to the incident and target project directories.





