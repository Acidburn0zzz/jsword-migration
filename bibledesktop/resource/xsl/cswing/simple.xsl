<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/TR/REC-html40" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" omit-xml-declaration="yes" indent="no"/>
  <xsl:strip-space elements="*"/>

  <xsl:param name="strongs.hebrew.url" select="'dict:'"/>
  <xsl:param name="strongs.greek.url" select="'dict:'"/>

  <!-- Whether to show Strongs or not -->
  <xsl:param name="strongs" select="'true'"/>

  <!-- The CSS stylesheet to use. The url must be absolute. -->
  <xsl:param name="css"/>
  
  <!-- The order of display. Hebrew is rtl (right to left) -->
  <xsl:param name="direction" select="'ltr'"/>

  <!--
  The font that is passed in is of the form: font or font,style,size 
  where style is a bit mask with 1 being bold and 2 being italic.
  This needs to be changed into a style="xxx" specification
  -->
  <xsl:param name="font" select="Serif"/>
  <xsl:variable name="aFont">
    <xsl:choose>
      <xsl:when test="substring-before($font, ',') = ''"><xsl:value-of select="$font"/>,0,16</xsl:when>
      <xsl:otherwise><xsl:value-of select="$font"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="fontfamily" select='concat("font-family: &apos;", substring-before($aFont, ","), "&apos;;")' />
  <xsl:variable name="fontsize" select="concat(' font-size: ', substring-after(substring-after($aFont, ','), ','), 'pt;')" />
  <xsl:variable name="styling" select="substring-before(substring-after($aFont, ','), ',')" />
  <xsl:variable name="fontweight">
    <xsl:if test="$styling = '1' or $styling = '3'"><xsl:text> font-weight: bold;</xsl:text></xsl:if>
  </xsl:variable>
  <xsl:variable name="fontstyle">
    <xsl:if test="$styling = '2' or $styling = '3'"> font-style: italic;</xsl:if>
  </xsl:variable>
  <xsl:variable name="fontspec" select="concat($fontfamily, $fontsize, $fontweight, $fontstyle)"/>

  <!--
  For now, we assume that all the works inside a corpus are of the
  same type.
  -->
  <xsl:variable name="osis-id-type" select="substring-before((//osisText)[1]/@osisIDWork, '.')"/>

  <xsl:variable name="page-div-type">
    <xsl:choose>
      <!--
      KJV is a special case. It should be Bible.KJV, but some OSIS
      transcriptions just use KJV instead.
      -->
      <xsl:when test="$osis-id-type = 'Bible' or $osis-id-type = 'KJV'">
        <xsl:text>chapter</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Dictionary'">
        <xsl:text>x-lexeme</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Lexicon'">
        <xsl:text>x-lemma</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Morph'">
        <xsl:text>x-tag</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>FIXME</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!--=======================================================================-->
  <xsl:template match="/osis">
    <html dir="{$direction}">
      <head>
        <xsl:if test="$css != ''">
          <link rel="stylesheet" type="text/css" href="{$css}" title="styling" />
        </xsl:if>
        <style type="text/css">
          BODY { <xsl:value-of select="$fontspec" /> }
          A { text-decoration: none; }
          A.strongs { color: black; text-decoration: none; }
          SUP.verse { font-size: 75%; color: gray; }
          SUP.note { font-size: 75%; color: green; }
          FONT.verse { }
          h3 { font-size: 110%; color: #666699; font-weight: bold; }
          h2 { font-size: 115%; color: #669966; font-weight: bold; }
          div.margin { font-size: 90%; }
        </style>
      </head>
      <body>
        <!-- If there are notes, output a table with notes in the 2nd column. -->
        <xsl:choose>
          <xsl:when test="//note">
            <xsl:choose>
              <xsl:when test="$direction != 'rtl'">
	            <table cols="2" cellpadding="5" cellspacing="5" width="100%">
	              <tr>
	                <td valign="top" width="80%">
	                  <xsl:apply-templates/>
	                </td>
	                <td valign="top" width="20%" style="background:#f4f4e8;">
	                  <p>&#160;</p>
	                  <xsl:apply-templates select="//note" mode="print-notes"/>
	                </td>
	              </tr>
	            </table>
              </xsl:when>
              <xsl:otherwise>
                <!-- reverse the table for Right to Left languages -->
	            <table cols="2" cellpadding="5" cellspacing="5" width="100%">
	              <!-- In a right to left, the alignment should be reversed too -->
	              <tr align="right">
	                <td valign="top" width="20%" style="background:#f4f4e8;">
	                  <p>&#160;</p>
	                  <xsl:apply-templates select="//note" mode="print-notes"/>
	                </td>
	                <td valign="top" width="80%">
	                  <xsl:apply-templates/>
	                </td>
	              </tr>
	            </table>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="osisCorpus">
    <xsl:for-each select="osisText">
      <!-- If this text has a header, apply templates to the header. -->
      <xsl:if test="preceding-sibling::*[1][self::header]">
        <div class="corpus-text-header">
          <xsl:apply-templates select="preceding-sibling::*[1][self::header]"/>
        </div>
      </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="osisText">
    <xsl:apply-templates/>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="div">
    <xsl:if test="@divTitle">
      <h1><xsl:value-of select="@divTitle"/></h1>
    </xsl:if>
    <xsl:if test="@type = 'testament'">
      <h2>
        <xsl:choose>
          <xsl:when test="preceding::div[@type = 'testament']">
           <xsl:text>New Testament</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Old Testament</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </h2>
    </xsl:if>
    <xsl:if test="@osisID">
      <a href="{@osisID}">
        <xsl:apply-templates/>
      </a>
      <!-- DMS: reading references need a following space -->
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:if test="not(@osisID)">
      <xsl:apply-templates/>
    </xsl:if>
    <xsl:if test="@divTitle">
      <p>&#0160;</p>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="verse">
    <!-- DMS: if the verse is not the first verse of a set of siblings, output two spaces. -->
    <xsl:if test="preceding-sibling::*[local-name() = 'verse']">
      <xsl:text> &#160;</xsl:text>
    </xsl:if>
    <xsl:variable name="title" select=".//title"/>
    <xsl:if test="string-length($title) > 0">
      <h3><xsl:value-of select="$title"/></h3>
    </xsl:if>
    <a name="{@osisID}"><sup class="verse"><xsl:value-of select="substring-after(substring-after(@osisID, '.'), '.')"/></sup></a>
    <font class="verse">
      <xsl:apply-templates/>
    </font>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="a">
    <a href="{@href}">
      <xsl:apply-templates/>
    </a>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="note">
    <!-- If the preceeding sibling was a note, emit a separator -->
    <xsl:if test="preceding-sibling::*[local-name() = 'note']">
      <sup class="note">,&#160;</sup>
    </xsl:if>
    <a href="#note-{generate-id(.)}">
      <sup class="note"><xsl:number level="any" from="/" format="a"/></sup>
    </a>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="note" mode="print-notes">
    <div class="margin">
      <b><xsl:number level="any" from="/" format="a"/></b>
      <a name="note-{generate-id(.)}">
        <xsl:text> </xsl:text>
      </a>
      <xsl:apply-templates/>
      [<a href="#{ancestor::verse[1]/@osisID}">verse</a>]
    </div>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="p" mode="print-notes">
    <!-- don't put para's in notes -->
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="w">
    <!-- FIXME: Handle all the other attributes besides lemma. -->
    <!-- FIXME: Only handles one x-StrongsNumber when more than one present -->
      <xsl:variable name="siblings" select="../child::node()"/>
      <xsl:variable name="next-position" select="position() + 1"/>
      <xsl:choose>
        <xsl:when test="$strongs = 'true' and starts-with(@lemma, 'x-Strongs:')">
          <xsl:variable name="orig-lemma" select="substring-after(@lemma, ':')"/>
          <xsl:variable name="strongs-type" select="substring($orig-lemma, 1, 1)"/>
          <xsl:variable name="first-lemma">
            <xsl:choose>
              <xsl:when test="contains($orig-lemma, '|')">
                <xsl:value-of select="substring-before($orig-lemma, '|')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$orig-lemma"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:choose>
            <xsl:when test="$strongs-type = 'H'">
              <a href="{$strongs.hebrew.url}{$first-lemma}" class="strongs"><xsl:apply-templates/></a>
            </xsl:when>
            <xsl:otherwise>
              <a href="{$strongs.greek.url}{$first-lemma}" class="strongs"><xsl:apply-templates/></a>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
      <!--
        except when followed by a text node or non-printing node.
        This is true whether the href is output or not.
      -->
      <xsl:if test="$siblings[$next-position] and name($siblings[$next-position]) != ''">
        <xsl:text> </xsl:text>
      </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="seg">
    <xsl:choose>
      <xsl:when test="@type='font-style: italic;'">
        <i><xsl:apply-templates/></i>
      </xsl:when>
      <xsl:when test="@type='font-weight: bold;'">
        <b><xsl:apply-templates/></b>
      </xsl:when>
      <xsl:when test="@type='text-decoration: underline;'">
        <u><xsl:apply-templates/></u>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'color:')">
        <font color="substring-before(substring-after(@type, 'color: '), ';')">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'font-size:')">
        <font size="substring-before(substring-after(@type, 'font-size: '), ';')">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:otherwise>
        <p><xsl:apply-templates/></p>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="abbr">
    <abbr class="abbr">
      <xsl:if test="@expansion">
        <xsl:attribute name="title">
          <xsl:value-of select="@expansion"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </abbr>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="speaker">
    <xsl:choose>
      <xsl:when test="@who='Jesus'">
        <font color="red">
          <xsl:apply-templates/>
        </font>
      </xsl:when>
      <xsl:otherwise>
        <font color="blue">
          <xsl:apply-templates/>
        </font>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="title">
    <h2>
      <xsl:apply-templates/>
    </h2>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="title[@type='section']">
  <!-- Done by a line in [verse]
    <h3>
      <xsl:apply-templates/>
    </h3>
  -->
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="reference">
    <a href="bible://{@osisRef}">
      <xsl:apply-templates/>
    </a>
  </xsl:template>
  
  <!--=======================================================================-->

  <xsl:template match="caption">
    <div class="caption">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="catchWord">
    <span class="catchWord">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!--
      <cell> is handled shortly after <table> below and thus does not appear
      here.
  -->
  
  <xsl:template match="closer">
    <div class="closer">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="date">
    <span class="date">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="divineName">
    <span class="divineName">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="figure">
    <div class="figure">
      <img src="@src"/>  <!-- FIXME: Not necessarily an image... -->
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="foreign">
    <em class="foreign">
      <xsl:apply-templates/>
    </em>
  </xsl:template>
  
  <!-- This is a subheading. -->
  <xsl:template match="head//head">
    <h5 class="head">
      <xsl:apply-templates/>
    </h5>
  </xsl:template>
  
  <!-- This is a top-level heading. -->
  <xsl:template match="head">
    <h4 class="head">
      <xsl:apply-templates/>
    </h4>
  </xsl:template>
  
  <xsl:template match="hi">
    <span class="hi">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="index">
    <a name="index{@id}" class="index"/>
  </xsl:template>
  
  <xsl:template match="inscription">
    <span class="inscription">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="item">
    <li class="item">
      <xsl:apply-templates/>
    </li>
  </xsl:template>
  
  <!--
      <item> and <label> are covered by <list> below and so do not appear here.
  -->
  
  <xsl:template match="l">
    <div class="l">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="lg">
    <div class="lg">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="list">
    <xsl:choose>
      <xsl:when test="label">
        <!-- If there are <label>s in the list, it's a <dl>. -->
        <dl class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::label">
                <dt class="label">
                  <xsl:apply-templates/>
                </dt>
              </xsl:when>
              <xsl:when test="self::item">
                <dd class="item">
                  <xsl:apply-templates/>
                </dd>
              </xsl:when>
              <xsl:when test="self::list">
                <dd class="list-wrapper">
                  <xsl:apply-templates select="."/>
                </dd>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </dl>
      </xsl:when>

      <xsl:otherwise>
        <!-- If there are no <label>s in the list, it's a plain old <ul>. -->
        <ul class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::item">
                <li class="item">
                  <xsl:apply-templates/>
                </li>
              </xsl:when>
              <xsl:when test="self::list">
                <li class="list-wrapper">
                  <xsl:apply-templates select="."/>
                </li>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="mentioned">
    <span class="mentioned">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <!--
      Note: I have not covered <milestone>, <milestoneStart>, or
            <milestoneEnd> here, since I have no idea what they are supposed
            to do, based on the spec.
  -->
  
  <xsl:template match="name">
    <span class="name">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  <xsl:template match="q">
    <!--
        FIXME: Should I use <span> here?  The spec says that this can be used
               as an embedded quote or a block quote, but there seems to be no
               way to figure out which it is based on context.  Currently I've
               got it as a <blockquote> because it has block-level elements in
               it.
        
        FIXME: Should I include the speaker in the text, e.g.:
               
                   {@who}: {text()}
               
               ?  I'm not sure.  Currently I've just got it as a "title"
               attribute on the <span>.
    -->
    <blockquote class="q">
      <xsl:if test="@who">
        <xsl:attribute name="title"><xsl:value-of select="@who"/></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>
  
  <xsl:template match="rdg">
    <div class="rdg">
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!--
      <row> is handled near <table> below and so does not appear here.
  -->
  
  <xsl:template match="salute">
    <div class="salute">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="signed">
    <span class="signed">
      <xsl:apply-templates/>
    </span>
  </xsl:template>

  <xsl:template match="speech">
    <div class="speech">
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <xsl:template match="table">
    <table class="table">
      <xsl:copy-of select="@rows|@cols"/>
      <xsl:if test="head">
        <thead class="head">
          <xsl:apply-templates select="head"/>
        </thead>
      </xsl:if>
      <tbody>
        <xsl:apply-templates select="row"/>
      </tbody>
    </table>
  </xsl:template>
  
  <xsl:template match="row">
    <tr class="row">
      <xsl:apply-templates/>
    </tr>
  </xsl:template>
  
  <xsl:template match="cell">
    <xsl:variable name="element-name">
      <xsl:choose>
        <xsl:when test="@role = 'label'">
          <xsl:text>th</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>td</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$element-name}">
      <xsl:attribute name="class">cell</xsl:attribute>
      <xsl:if test="@rows">
        <xsl:attribute name="rowspan">
          <xsl:value-of select="@rows"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@cols">
        <xsl:attribute name="colspan">
          <xsl:value-of select="@cols"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="transChange">
    <span class="transChange">
      <xsl:apply-templates/>
    </span>
  </xsl:template>
  
  
  <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
      Named templates
   - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
  
  <xsl:template name="trim-zeros-from-number">
    <xsl:param name="number" select="'0'"/>
    <xsl:value-of select="string(number($number))"/>
  </xsl:template>
  
  <xsl:template name="print-prev-next-links">
    <xsl:param name="div" select="."/>
    
    <xsl:variable name="previous-section" select="$div/preceding::div[@type = $page-div-type][1]"/>
    <xsl:variable name="next-section" select="$div/following::div[@type = $page-div-type][1]"/>
    <xsl:if test="$previous-section or $next-section">
      <table width="100%" class="navigation">
        <tr>
          <xsl:if test="$previous-section">
            <td align="left">
              <a href="{$previous-section/@osisID}.html" class="previous-link">[&lt; Previous]</a>
            </td>
          </xsl:if>
          <xsl:if test="$next-section">
            <td align="right">
              <a href="{$next-section/@osisID}.html" class="next-link">[Next &gt;]</a>
            </td>
          </xsl:if>
        </tr>
      </table>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>
