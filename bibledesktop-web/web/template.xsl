<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="html jsp"
    >

<xsl:output
    method="xml"
    indent="yes"
    encoding="windows-1252"
    />
    <!--
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
    -->

<xsl:template match="/jsp:root">
  <jsp:root version="1.2">
    <xsl:apply-templates/>
  </jsp:root>
</xsl:template>

<xsl:template match="html:html">

<html>
  <head>
    <link rel="stylesheet" type="text/css" href="sword.css"/>
    <xsl:copy-of select="html:head/*"/>
    <script language="javascript">
      <![CDATA[
      var thispage = location.href.substring(location.href.lastIndexOf("/")+1);
      function writeLink(parentName, dest, title)
      {
        var parent = document.getElementById(parentName);
        if (dest == thispage)
        {
          var div = document.createElement("div");
          parent.appendChild(div);
          div.setAttribute("class", "lsidecell current");
          div.appendChild(document.createTextNode(title));
        }
        else
        {
          var div = document.createElement("div");
          var a = document.createElement("a");
          parent.appendChild(div);
          div.setAttribute("class", "lsidecell pagelink");
          div.appendChild(a);
          a.setAttribute("href", dest);
          a.appendChild(document.createTextNode(title));
        }
      }

      function emptyIf(id, ifval)
      {
        var textele = document.getElementById(id);
        if (textele.value == ifval)
        {
          textele.value = "";
        }
      }
      ]]>
    </script>
  </head>
  <body>
    <xsl:copy-of select="html:body/@*"/>

    <xsl:copy-of select="html:body/*"/>

    <div id="side">
      <script language="javascript">
        <![CDATA[
        writeLink("side", "index.html", "Home");
        writeLink("side", "download.html", "Download");
        writeLink("side", "screenshot.html", "Screenshots");
        ]]>
      </script>

      <p>
        Related Projects:<br/>
        <a href="/jsword">J-Sword</a><br/>
        <a href="/sword">Sword</a>
      </p>

      <div class="lsidecell subscribe">
        <form method="post" action="http://www.crosswire.org/mailman/subscribe/jsword-devel">
          <input type='hidden' name="digest" value="0"/>
          Stay informed:<br/>
          <input type="text" size="10" id="email" name="email" value="your e-mail" onfocus="emptyIf('email', 'your e-mail');"/>
          <br/>
          <input type="submit" name="email-button" value="Subscribe"/>
        </form>
      </div>
    </div>

   </body>
</html>

</xsl:template>

</xsl:stylesheet>
