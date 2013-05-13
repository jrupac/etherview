etherview
=========

A visualization of the original Ethernet protocol.

Compilation
-----------

JDK 7 is required to compile the project. To compile, run:

    javac -cp src/ src/Runner.java

Execution
---------

`Runner` is the entry point into the program. Run using

    java -cp src/ Runner [< inputFile]

Usage
-----

### GUI

A GUI will appear, allowing the user to select hosts
to send and receive packets to. Once the start button
is clicked, the packets will be animated in the
visualization window.

Pause and play buttons are also available. Once the animation
is paused, the step button is activated, allowing the user to
step through the animation a frame at a time. A slider is
also available to increase the animation speed.

During the visualization, each host displays a status
message indicating what state it is in, and some supplemental
information about the state. Each host is represented by
a unique color, and all packets sent by a host are of
this color.

Note: The number of hosts is fixed at 3, to reduce clutter
in the visualization.

### Input File

The visualization takes as input a series of packets to send between
hosts. These packets will be displayed in the visualizer. The format
of the input file is a series of lines containing 4 integers:

    <frame in which to send packet> <sender ID> <receiver ID> <packet length>

* Frame: must be greater than or equal to 0.
* sender ID: must be in the range [0,2]
* receiver ID: must be in the range [0,2]
* packet length: must be greater than 50, the minimum packet length

Note: The packet length is in units of "cells." Each cell in our simmulation
represents the distance that an Ethernet signal can travel in one time unit.
The minimum packet length is 50 because we fixed the maximum ether length
at 25 cells.

### Sample Input File

    3 2 1 55
    5 1 0 62

Drawing Library
---------------

We used and adapted the `StdDraw.java` library from the [Princeton COS 126
course materials.](http://introcs.cs.princeton.edu/java/stdlib/)
