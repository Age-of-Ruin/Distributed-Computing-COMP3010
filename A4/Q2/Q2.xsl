<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<h1>Video Website Collection</h1>  
				<xsl:apply-templates/>  
			</body>
		</html>
	</xsl:template>

	<xsl:template match="Website">
		<h3>
			<xsl:apply-templates select="URL"/>  
			<xsl:apply-templates select="CommonName"/>
			<xsl:apply-templates select="Reliability"/> 
		</h3>

		<xsl:apply-templates select="VideoLibrary"/>  

	</xsl:template>

	<xsl:template match="URL">
		URL: <span style="color:#ff0000"><xsl:value-of select="."/></span>
		<br />
	</xsl:template>

	<xsl:template match="CommonName">
		Common Name: <span><xsl:value-of select="."/></span>
		<br />
	</xsl:template>

	<xsl:template match="Reliability">
		Reliability: <span style="font-style:italic"><xsl:value-of select="."/></span>
	</xsl:template>

	<xsl:template match="VideoLibrary">
		
		<h4>List of Movies</h4>

		<div><xsl:apply-templates select="Video"/></div> 

	</xsl:template>

	<xsl:template match="Video">
		<span>
			Title: <xsl:value-of select="VideoTitle"/> --
			Runtime: <xsl:value-of select="VideoRuntime"/> minutes --
	    	Encoding Format: <xsl:value-of select="VideoEncodingFormat"/>
		</span>
		<br /> 

	</xsl:template>

</xsl:stylesheet>