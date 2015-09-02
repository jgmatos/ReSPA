



0) Content: In the repository we have:

0.1) The source code of ReSPA
0.2) A compiled runnable jar of ReSPA: ReSPA.jar
0.3) A compiled runnable jar of Java PathFinder: RunJPF.jar
0.4) A hidden directory .respa
0.5) A zipped folder Example_vt-password.zip


1) .respa: The first step is to copy/move the directory .respa to user's home. This directory should contain:

	1.1) A directory of the crash incident, which should contain:

		1.1.1) A txt file containing the stack trace of the observed failure
		
		1.1.2) A txt file containing the input that led to the failure
		
		1.1.3) A txt file containing the where in the code the input is read
		
		1.1.4) A ReSPA.jpf configuration file. This is actually a JPF requirement
		
	1.2) a config directory respaConfig, which should contain:

		1.2.1) A respa.properties file with options for respa
		
		1.2.2) A log.properties file with logging options (not mandatory)
		
2) Then, copy/move RunJPF.jar to .respa

3) For exemplification purposes, unzip Example_vt-password.zip	and move the resulting folder to your workspace
	
4) Finally, open a terminal, go to the directory where you keep ReSPA.jar and run:	

	java -jar ReSPA.jar path/to/incidentDir path/to/SUTdir

where args[0] is the path to the incident dir described in 1.1 and args[1] is the path to the system under test.
For the example provided, the incident dir should be /path/to/user/home/.respa/incident207
whereas the SUTdir should be /path/to/user/workspace/Example_vt-password.







######################################## ReSPA config ########################################

In the incident directory /path/to/user/home/.respa/incident?/ you should find a file called respa.properties
In this file you may configure the behavior of ReSPA, e.g. set the radius.




######################################## F ########################################

The file /path/to/user/home/.respa/incident?/exceptionStackTrace.txt is mandatory. It should contain the 
stack trace of the observed failure. This file is what allows ReSPA to uniquely identify F.

######################################## Input and Symbolic variables ########################################

We provide three ways to intersect input by symbolic variables. The example provided uses the most simple one:

	1) /path/to/user/home/.respa/incident?/input.txt contains the input that led to F.
	2) /path/to/user/home/.respa/incident?/inputLocations.txt indicates where in the source code, the input variables 
	should be replaced by symbolic variables (one per line)
	3) /path/to/user/home/.respa/incident?/respa.properties contains an entry input_detection. Set is as false.
	
This method is not optimal for some scenarios. For example, if the input is a File input stream, then an input location
is read multiple times in a loop. For this situation, you should:

	1) create a folder FileIn at /path/to/user/home/.respa/incident?/
	2) Place the input File (e.g. myFinducingFile.xml) inside /path/to/user/home/.respa/incident?/FileIn
	3) /path/to/user/home/.respa/incident?/respa.properties contains an entry input_detection. Set is as false.
	4) edit the input file and place in the very first line, the input location as done for the previous step
	
Another way is simply to set input_detection to true in /path/to/user/home/.respa/incident?/respa.properties. This option
triggers automatic input detection and intersection. Note, however that this option requires numerous fixes and
does not yet work in many scenarios (work in progress). 


######################################## Output ########################################

In the directory .respa/respaConfig there should be a file log.properties. You can edit this file to
configure what should/shouldn't be logged and printed in System.out.
The log is recorded to the file log.txt, which is placed within path/to/SUTdir/.respa/tmp/log.txt

ReSPA prints the alternative input in System.out and also to a file alternativeInput.txt within path/to/SUTdir/.respa/tmp/.
Privacy information such as leakage and residue (and also statistical behavior of ReSPA) should also be
printed to System.out and to path/to/SUTdir/.respa/tmp/log.txt.












######################################## Source Code ########################################



	
input: The input package contains classes used to parse the input of the user and to represent the symbolic variables;
leak: The leak package composes all methods used to calculate the leakage of a path condition;
log: All classes used for logging operations. Mainly relevant for evaluation and debugging purposes; 
main: The ReSPA algorithm
output: Classes required to create an alternative input from the path condition.








