
ids="@id='RWS01_MONIBAS_0201hrr0197ra' or @id='RWS01_MONIBAS_0201hrr0200ra'"
zcat $1 | 
	xgrep -n d="http://datex2.eu/schema/2/2_0" \
		-n s="http://schemas.xmlsoap.org/soap/envelope/" \
		-x "/s:Envelope/s:Body/d:d2LogicalModel/d:payloadPublication/d:siteMeasurements[d:measurementSiteReference[$ids]]"