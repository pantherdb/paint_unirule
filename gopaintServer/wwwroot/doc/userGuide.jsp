
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="/css/style.css" type="text/css" rel="stylesheet">        
        <title>Unirule PAINT User Guide</title>
    </head>
    <body>
        <div id="mainbody">
            <h1>Unirule PAINT User Guide</h1>
            <p>
                UniRule PAINT is a Java software application that allows curators to add annotations to ancestral nodes in a phylogenetic tree and propagate the annotations to present day protein sequences. The PAINT client tool enables curators to view both GO annotations and UniRule annotations in a phylogenetic context. It allows curators to create groupings (cases) of UniRule annotations if one or more annotations have been associated with the same set of sequences. 
            </p>
            <h2>Table of Contents</h2>
            <a href="#I.">I.Getting started</a><BR> 
            <a href="#II.">II.Manage Books Dialog</a><BR>
            <a href="#III.">III.Viewing Books and updating annotations</a><BR>
            <a href="#III.A.">III.A.Tree Panel</a><BR>
            <a href="#III.B.">III.B.Protein Information Panel</a><BR> 
            <a href="#III.C.">III.C.MSA Panel</a><BR> 
            <a href="#III.D.">III.D.Annotation Matrix Panel</a><BR>
            <a href="#IV.">IV.Viewing and Saving Annotations</a>
            <BR><BR><BR><BR>        
            <b><a name="I.">I. Getting Started</a></b><BR>
            <b>Requirements</b>
            <p>Java 1.11 or greater on either Windows or Macintosh.  Please refer to <a href="/doc/Installation.jsp"> instructions</a> for installing certificate from server.</p>
            <BR>
            <b>Launching UniRule PAINT</b>
            <ol>    
                <li>On a Windows machine, run the program lauchPAINT.bat</li>
                <li>On a Mac, open a Unix terminal window, go to the directory containing the PAINT program, and execute the command:sh launchPAINT.sh OR ./launchPAINT.sh</li> 
            </ol>
            <BR>
            <b>Menu</b>
            <ol>
                <li>The PAINT software connects to the UniRule server to retrieve information from UniRule. Use the Edit-> Edit UniRule Server-> Switch To Development/Switch to Production menu items to switch between the production and development Unirule server. If the system is retrieving and updating information from the UniRule development server, the menu bar will be colored in Pink to indicate caution.</li>
                <li>The user has to be logged into the system to view trees. Use Menu item File->Login to bring up the login dialog.</li>
                <li>Enter the user name and password and submit.</li>
                <li>Use the File->Manage Books to bring up the Manage Books Dialog.  This dialog is used to search for books, lock, unlock and select books for viewing and updating.</li>
            </ol>
            <BR><BR><BR>
            <b><a name="II.">II. Manage Books Dialog</a></b>
            <ol>        
                <li>Enter book id or search term and select the appropriate radio button to search for books and submit.</li>
                <li>The system will display a list of books from the search criteria.</li>
                <li>Click on the title bar column of the book list to sort.</li>
                <li>Books may have the following statuses:
                    <ul>
                        <li>Unknown - A user can update a book with status of unknown and save.  Once, saved the book will have a status</li>
                        <li>Apply</li>
                        <li>Disused</li>                    
                        <li>Test</li>
                    </ul>
                </li>
                <li>Depending on user privilege, the lock column will be colored in red, yellow or green. Where red is for user does not have privilege to lock, yellow for caution, since someone else has already locked the book, but, user has privilege to override and green for safe to lock and update.</li>
                <li>Click the appropriate check boxes, to lock/unlock and click 'Lock or Unlock Selected Books'.  The system, will attempt to lock or unlock and display the applicable list of books.  Note, the 'Locked by' column will be updated with the user who is currently locking the book. Note, if a book has status 'Unknown', then the book can be saved without locking.</li>
                <li>Click on the 'View Locked Books' tab, to view books that have been locked by the user who is currently logged. The books in this list can also be unlocked by clicking on the check box and 'Unlock selected books'.</li>
                <li>Click on 'View' to open the book.</li>
                <li>A book can be curated only if it has one or more sequences that have been reviewed by swissprot.</li>
            </ol>
            <BR><BR><BR>
            <b><a name="I.">III. Viewing Books and updating annotations</a></b>
            <BR>
            <b>Layout</b>
            <ol>        
                <li>The system will bring up the tree on the left panel.</li>
                <li>The protein information panel, the GO and UniRule annotation panels and the MSA panel are displayed on the right of the tree panel.</li>
            </ol>
            <ul>    
            <li>There are three panels with information about the tree being viewed/edited:  'Protein Family' - Displays Phylogenetic tree, associated protein information, associated multiple sequence information, associated PFAM domain, associated active site information and associated GO and UniRule annotations, 'Annotations' - displays annotations for the currently selected node and 'Evidence' - provides a summary of the annotations and comments, warnings or errors for the currently opened tree.</li>

            <li>Use the File->'Update UniRule comments' menu item to update internal and external comments for the tree.</li>

            <li>Updates can be saved to the uniRule data store using File->'Save UniRule annotations'.  Once saved, these can be viewed in the UniRule editing system.</li> 

            <li>Unirule information can also be saved locally using File->'Export UniRule annotations to XML file locally' and specifying a file name.</li>

            <li>If there are any errors when the tree is opened, the system will display a message.  These can be viewed using File->View UniRule annotation errors and File->View UniRule case errors</li>

            <li>Search for nodes using protein id, stable id, gene name and gene symbol via Edit->Find....  The system can search for multiple nodes separated by comma. Click on the result to highlight the node in the tree.</li>
            </ul>
            <BR><BR><BR>
            <b><a name="III.A.">III.A. Tree Panel</a></b>
            <p>
            The phylogenetic tree is displayed on the 'Tree' panel. Coloring of labels in tree when the Unirule tab is selected is based on if there are UniProt annotations associated with the node and if the sequence is marked as reviewed in swissprot:
             
            <ul>
                <li>Labels for non-reviewed nodes with no Unirule annotations i.e. direct or propagated are black.</li>
                <li>Labels for reviewed nodes with direct Unirule annotations are dark green.</li>
                <li>Labels for non-reviewed nodes with direct Unirule annotations and no propagated annotations are orange.</li>
                <li>Labels for nodes without direct Unirule annotations, but with propagated Unirule annotations are navy blue. If an annotation is propagated to a non-reviewed node (previously with black or orange label), its label color will change to navy blue.</li>
            </ul>
            </p>
            <ol>        
                <li>Left click on a node to highlight the node in the tree and the protein panel.</li>
                <li>Right clicking will bring up a popup menu to collapse/expand, reroot, output sequence ids for descendants.</li>           
            </ol>
            <p>The nodes are displayed as follows:</p>
            <ol>
                <li>Triangle if tree is re-routed to node other than root</li>
                <li>Square - Duplication event</li>
                <li>Circle - Speciation event</li>
                <li>Diamond - Horizontal transfer</li>
                <li>Colored Grey - pruned</li>            
            </ol>
            <p>Mousing over node will bring up tooltip with subfamily id and label, if applicable, followed by AN id and PTN id</p>
            <p>Nodes with a 'NOT' annotation will have a red bar on the branch</p>
            <img src="/images/unirule_matrix/not_annotation.png" alt="NOT Annotation"><BR> 
            <p>The Tree menu item from the menu bar can be used for the following:</p>
            <ol>
                <li>Expanding all nodes in tree, if any of the nodes have been collapsed.</li>
                <li>Collapse nodes without experimental data</li>
                <li>Reset Root to main after resetting root to descendant clade.</li>
                <li>Scaling the branch length</li>
                <li>Ordering leaves by species, laddering</li>
                <li>Coloring background by duplication or species</li>
            </ol>
            <BR><BR><BR>        
            <b><a name="III.B.">III.B. Protein Information Panel</a></b>
            <BR><BR><BR>
            <p>
            The 'Protein Information' panel displays the Uniprot id, gene database, gene id, species, PANTHER permanent id, protein definition, ECNum (from accs_annotaions_report file), CCFU (from accs_annotaions_report file), DEAF (from accs_annotaions_report file)and RHEA (from accs_annotaions_report file).
            <img src="/images/unirule_matrix/protein_info_panel.png" alt="Protein information panel"><BR>             
            <BR>
            </p>
            <BR><BR><BR>
            <b><a name="III.C.">III.C. MSA Panel</a></b>        
            <p>
            The 'MSA' panel displays multiple sequence, PFAM domain and key residue information.  Use the 'MSA and Domain' menu item and submenu items to select the information to be displayed.
            </p>
            <p>
            The Thresholds for MSA coloring can be updated using MSA drop down menu.
            </p>
            <p>
            Clicking on the pFam domains will open browser to pFam domain information page.
            </p>

            Key residue positions are colored as follows:        
            <ol>

                <li>Active site – black</li>
                <li>Binding – Red</li>
                <li>Metal – Orange</li>
                <li>Multiple residue types – Magenta</li>
            </ol>
            <BR><BR><BR>
            <b><a name="III.D.">III.D. Annotation Matrix Panel</a></b>        
            <p>
            The 'Annotation Matrix' panel displays both GO molecular function, GO biological process, GO cellular component annotations and UniRule annotations. The GO annotations cannot be modified.  The GO annotation panel contains the GO annotations from the previous GO release.  The release date can be verified from the website landing page.
            </p>

            <p>
            UniRule annotations can be associated with nodes by dragging annotations from the UniRule panel to a node in the tree panel.  When a tree node is annotated with a Unirule annotation, the annotation is propagated to all descendant nodes and the matrix is updated to reflect the annotations that are propagated. The propagation can be stopped at a descendant node by selecting the node, and clicking on the 'NOT' checkbox in the Associations panel. Click on the 'Delete' button to delete annotations.
            </p>

            <b>Sequence Information</b>
            <ol>
                <li>There are two main categories of sequences:
                    <ul>
                        <li>Sequences that have been reviewed by Swissprot - In the Protein Information panel, there is a 'Reviewed' column.  Sequences that are reviewed will have a check in this column. These are also colored in dark green with bold font<BR>
                            <img src="/images/unirule_matrix/experimental_annot.png" alt="Experimental Annotation"> 
                        </li>
                        <li>Sequences that have not been reviewed by Swissprot. Without any paint or UniRule annotations, these are colored black.  If these have UniRule annotations from the annotations report file, they are colored in orange with bold font.  Once, annotated with one or more paint annotations, it will be colored in navy in both tree and applicable column(s) in UniRule annotation matrix.<BR>
                            <img src="/images/unirule_matrix/unirule_annot.png" alt="Unirule Annotation">
                        </li>
                    </ul>
                </li>
                <li>Any rule marked with a 'NOT' will be colored in red in the matrix.  Also the tree image will have a red bar in the Tree panel.</li>
            </li>For PAINT to permit a curator to add UniRule annotations for a family, the family has to have one or more sequences which has been reviewed by Swissprot.</li>
            </ol>
            <BR><BR><BR>

            <b>Unirule Annotation matrix</b>
            <p>A family can have 4 types of annotations (rules):</p>
            <ol>        
                <li>These are annotation terms that have been labelled in UniRule.  These are labelled "URnnnnnnnnn" where n is a digit between 0 and 9. Colored in light grey and light blue to differentiate groups. These appear first in the matrix.</li>
                <li>Annotations from the Swissprot stats file are labelled "URNEW...". Colored dark grey and sea blue to differentiate groups.  These annotations can be dragged to nodes in the tree to annotate node with annotation.</li>
                <li>Annotations read from UniRule server labelled "URSWISS...". Colored in moss and light yellow to differentiate groups.  These annotations can be dragged to nodes in the tree to annotate node with annotation.</li>
                <li>Annotations read from UniRule server that should be labelled "URSWISS...", but cannot be added due to error. Colored in mauve and lavender to differentiate groups. These annotations can be dragged to nodes in the tree to annotate node with annotation.</li>
            </ol>
            <BR><BR><BR>
            <b>Types of rules and groupings</b>
            <ol>        
                <li>Related rules are grouped together and colored the same.</li>
                <li>The same rule can appear multiple times for a given family.</li>
                <li>Rules can be grouped into cases, if the rules share the same sequences. To differentiate the same rule appearing in multiple cases, the dots in the matrix are grey if the rule is not part of the case and white if the rule is part of the case.</li>
                <li>If a rule appears in multiple cases, the header is green in color</li>
                <li>If there is an exception to the rule, the header is red in color
                    <BR>
                    <img src="/images/unirule_matrix/annotation_exception.png" alt="Annotation Exception">                    
                    <BR>
                </li>
                <li>If a rule appears in multiple cases and has an exception, the header is orange.</li>
            </ol>
            <p>Detailed information about the rule can be obtained via tooltip by mousing over the rule</p>
            <BR><BR><BR>
            <b><a name="IV.">IV. Viewing and Saving Annotations</a></b>
            <p>Users have the following privileges:</p>
            <ol>        
                <li>Observer - Privilege to only view books</li>
                <li>User - Privilege to view and update unlocked books that have no status or books that have been updated by the same user</li>
                <li>Curator - Privilege to view and update unlocked books</li>
                <li>Admin - Privilege to view and update all books</li>
            </ol>    
        

        </div>
    </body>
</html>
