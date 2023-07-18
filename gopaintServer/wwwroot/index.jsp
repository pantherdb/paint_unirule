<%@page import="com.sri.panther.paintServer.util.ConfigFile"%>
<%@page import="com.sri.panther.paintServer.datamodel.FullGOAnnotVersion"%>
<%@page import="com.sri.panther.paintServer.logic.VersionManager"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">        
        <link href="/css/style.css" type="text/css" rel="stylesheet">
    </head>    
    <body>
        <div id="mainbody">    
        <%
            VersionManager vm = VersionManager.getInstance();
            FullGOAnnotVersion fgv = vm.getFullGOAnnotVersion();
        %>
        <img src="/images/panther/panther_sm.gif" align=center>
        <BR><BR><BR>
        <h3>Welcome UniRule PAINT Curators</h3>
        <p>Click on the following <A HREF="downloads/paintApp.tar"> link </A> to download the PAINT (Phylogenetic tree Annotation INference Tool) application. Installation instructions are available <a href="/doc/Installation.jsp">here</a>. Please refer to the <a href="/doc/userGuide.jsp">user guide</a> for details about using the UniRule PAINT tool</p>
        <BR>
        <BR>
        <h3>UniRule data</h3>
        <p>Based on <a href="<%=ConfigFile.getProperty("url_unirule")%>">annotation report</a></p>
        <p>Based on <a href="<%=ConfigFile.getProperty("url_swissprot_stats")%>">swissprot stats report</a></p>
        <BR>
        <BR>
        <h3>Geneontology data</h3>
        <p>This is UPL Version <%=vm.getPantherVersion().getId()%> with Full GO Version <%=fgv.getId()%> released on <%=fgv.getReleaseDate()%></p>
        <BR>
        <BR>
        <p>PAINT services information is available from <a href="/services/index.jsp">here</a>.</p>
        <BR>
        <BR>
        <p>Consistency checks information is available from <a href="/validation/index.jsp">here</a>.</p>
        </div>
    </body>
    </html>