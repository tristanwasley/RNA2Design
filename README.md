# RNA2Design (BioE 134 Final Project, December 2017)

User interface for finding the most optimally folding RNA sequence, given a drawn secondary structure.

Background:

    This project stems from the current inablity to simply read DNA sequences and be able to understand what
    they do. There is a similar problem with correlating structure to sequence. The idea is to create a 
    platform with-in which Bioengineers can design biologically-sound RNA secondary structures, knowing only
    what shape or functionality they need.

To run RNA2Design, either:

    1. ) Clone the repository and run drawdiag.java from an IDE like IntelliJ.
    or,
    2. ) Locate the RNA2Design.jar inside of the file labelled "out".
          a. ) Download to a convenient location on your computer and double click the file to run it.
                *Instructions on how to draw structures are provided in program.
                
Instructions:

    1. ) Start by clicking around in the graph, using the commands listed below.
    2. ) After finishing your structure and making sure you've made all the bonds you want, click the Predict
          button.
    3. ) Given your structure had no pseudoknots, the program will output an initial sequence with a dot-Bracket 
          sequence as well. You may click this again to test a slightly different configuration via chemical intuition
          and random numbers.
    4. ) Proceeding to the next stage, click the "Optimize" button next. Currently, this may pause the program then do 
          nothing. At this point, you can either repeat clicking "Optimize" and waiting for it to give a good sequence, or 
          repeat the process from step 2. ), no need to clear the graph and remake the structure.
    

Potential Areas of Improvement:

    1. ) Integrate an algorithm like Zuker's to handle prediction of a sequence's structure via kinetics.
    2. ) Add more graphics to the drawing function.
    3. ) Decrease the amount of clicks one needs to do to make a structure.
    4. ) Ability to add "reactive" sites to specific parts of the RNA structure.
      a. ) i.e. Hammerhead Ribozyme, RNA logic circuit
    5. ) 
  
  
Credits:

    Tristan Wasley
    VARNA v3.93 - All classes relating specifically to the running of the Nussinov algorithm.
