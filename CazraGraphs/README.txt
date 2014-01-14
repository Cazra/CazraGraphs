README
========

/*======================================================================
 * 
 * Cazra Graphs : Interactive data structure visualizations for Java
 * 
 * Copyright (c) 2013 by Stephen Lindberg (sllindberg21@students.tntech.edu)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
======================================================================*/

v 2.0

Cazra Graphs is a collection of data structure visualizations written with 
my Pwnee2D Java game engine. 

Currently it provides frameworks for visualizing the following data structures: 
Graphs (directed and undirected)
Trees

When updates are made that are not backwards-compatible, the major version 
number is incremented and the minor version number is set back to 0. 
For backwards-compatible updates, the minor version number is incremented.

Building the class files/jar from the source code:
-------------------
Open a command terminal in the "development" directory and enter the command
"ant"

This will create the cazgraphs.jar in the "latest" directory and it will also 
create the Javadocs docs in the "docs" directory. The class
files will be created in the "bin" directory.


Interactive directed graph maker
--------------------
The main target for cazgraphs.jar is an interactive directed graph maker.
In it you can double-click in empty space to add nodes, and you can drag 
the right mouse button to form edges between existing nodes. 
Nodes can be dragged and dropped with the left mouse button.

If you provide a file path as an argument to cazgraphs.jar, then it will
produce a graph from the input file, provided that the file is in the following 
format:

--
vertices
--
[new line-separated list of vertex names]
--
edges
--
[new line separated list of edges folling this format:
 source vertex -> [comma-separted list of target vertices]
]

