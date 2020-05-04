<?xml version="1.0" encoding="ISO-8859-1"?>
<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld"
  xmlns:sld="http://www.opengis.net/sld" xmlns:ogc="http://www.opengis.net/ogc"
  xmlns:xlink="http://www.w3.org/1999/xlink"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.opengis.net/sld http://schemas.cubewerx.com/schemas/sld/1.0.0-cw/StyledLayerDescriptor.xsd">
  <NamedLayer>
    <Name>co99_d00</Name>
    <UserStyle>
      <Name>States</Name>
      <IsDefault>1</IsDefault>
      <FeatureTypeStyle>
        <FeatureTypeName>co99_d00</FeatureTypeName>
        <Rule>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
						<Fill>
							<CssParameter name="fill">#FFFFFF</CssParameter>
						</Fill>
          </PolygonSymbolizer>
          <!--
					<TextSymbolizer>
						<Label>
							<ogc:PropertyName>COUNTY</ogc:PropertyName>
						</Label>
						<Font>
							<CssParameter name="font-style">normal</CssParameter>
						</Font>
					</TextSymbolizer>
					-->
        </Rule>
        <Rule>
					<Filter>
						<ogc:And>
							<ogc:PropertyIsEqualTo>
								<ogc:PropertyName>STATE</ogc:PropertyName>
								<ogc:Literal>26</ogc:Literal>
							</ogc:PropertyIsEqualTo>
							<ogc:PropertyIsEqualTo>
								<ogc:PropertyName>COUNTY</ogc:PropertyName>
								<ogc:Literal>161</ogc:Literal>
							</ogc:PropertyIsEqualTo>
						</ogc:And>
					</Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
						<Fill>
							<CssParameter name="fill">#00FF00</CssParameter>
						</Fill>
          </PolygonSymbolizer>
					<TextSymbolizer>
						<Label>26161</Label>
						<Font>
							<CssParameter name="font-style">normal</CssParameter>
						</Font>
					</TextSymbolizer>
        </Rule>
        <Rule>
					<Filter>
						<ogc:And>
							<ogc:PropertyIsEqualTo>
								<ogc:PropertyName>STATE</ogc:PropertyName>
								<ogc:Literal>18</ogc:Literal>
							</ogc:PropertyIsEqualTo>
							<ogc:PropertyIsEqualTo>
								<ogc:PropertyName>COUNTY</ogc:PropertyName>
								<ogc:Literal>161</ogc:Literal>
							</ogc:PropertyIsEqualTo>
						</ogc:And>
					</Filter>
          <PolygonSymbolizer>
            <Stroke>
              <CssParameter name="stroke">#000000</CssParameter>
              <CssParameter name="stroke-width">1</CssParameter>
            </Stroke>
						<Fill>
							<CssParameter name="fill">#FF0000</CssParameter>
						</Fill>
          </PolygonSymbolizer>
					<TextSymbolizer>
						<Label>18161</Label>
						<Font>
							<CssParameter name="font-style">normal</CssParameter>
						</Font>
					</TextSymbolizer>
        </Rule>
 <!--
				<Rule>
					<TextSymbolizer>
						<Label>
							<ogc:PropertyName>COUNTY</ogc:PropertyName>
						</Label>
						<Font>
							<CssParameter name="font-style">normal</CssParameter>
						</Font>
						<Halo>				    
							<Radius>
								<ogc:Literal>2</ogc:Literal>
							</Radius>
							<Fill>
								<CssParameter name="fill">#FFFFFF</CssParameter>
								<CssParameter name="fill-opacity">1</CssParameter>				
							</Fill>
						</Halo>
						<VendorOption name="group">yes</VendorOption>
					</TextSymbolizer>
				</Rule>
-->
      </FeatureTypeStyle>
    </UserStyle>
  </NamedLayer>
</StyledLayerDescriptor>
