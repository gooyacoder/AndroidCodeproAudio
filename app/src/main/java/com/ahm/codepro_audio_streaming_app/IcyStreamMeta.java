package com.ahm.codepro_audio_streaming_app;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IcyStreamMeta
{
    protected URL streamUrl;
    private Map<String, String> metadata;
    private boolean isError;
    private Map<String, String> data;

    public IcyStreamMeta()
    {
        isError = false;
    }

    /**
     * Get artist using stream's title
     *
     * @return String
     * @throws IOException
     */

    public String getArtist() throws IOException {
        data = getMetadata();

        if(data != null){

            if (!data.containsKey("StreamTitle"))
                return "";

            String streamTitle = data.get("StreamTitle");
            //String title = streamTitle.substring(0, streamTitle.indexOf("-"));
            String title;
            if(streamTitle.contains("-")){
                title = streamTitle.length() > 0 ? streamTitle.substring(0, streamTitle.indexOf("-")) : " ";
            }
            else{
                title = streamTitle;
            }

            return title.trim();
        }
        else {
            return "";
        }

    }

    /**
     * Get streamTitle
     *
     * @return String
     * @throws IOException
     */
    public String getStreamTitle() throws IOException
    {
        data = getMetadata();

        if (!data.containsKey("StreamTitle"))
            return "";

        return data.get("StreamTitle");
    }

    /**
     * Get title using stream's title
     *
     * @return String
     * @throws IOException
     */
    public String getTitle() throws IOException {
        data = getMetadata();
        if(data != null){
            if (!data.containsKey("StreamTitle"))
                return "";

            String streamTitle = data.get("StreamTitle");
            //String artist = streamTitle.substring(streamTitle.indexOf("-")+1);
            String artist;
            if(streamTitle.contains("-")){
                artist = streamTitle.length() > 0 ? streamTitle.substring(streamTitle.indexOf("-")+1) : " ";
            }
            else{
                artist = "";
            }
            return artist.trim();
        }
        else {
            return "";
        }


    }

    public Map<String, String> getMetadata() throws IOException {
        if (metadata == null) {
            refreshMeta();
        }

        return metadata;
    }

    synchronized public void refreshMeta() throws IOException
    {
        retreiveMetadata();
    }

    synchronized private void retreiveMetadata() throws IOException
    {
        URLConnection con = streamUrl.openConnection();
        con.setRequestProperty("Icy-MetaData", "1");
        con.setRequestProperty("Connection", "close");
        con.setRequestProperty("Accept", null);
        con.connect();
        int metaDataOffset = 0;
        Map<String, List<String>> headers = con.getHeaderFields();
        InputStream stream = con.getInputStream();

        if (headers.containsKey("icy-metaint")) {
            // Headers are sent via HTTP
            metaDataOffset = Integer.parseInt(headers.get("icy-metaint").get(0));
        } else {
            // Headers are sent within a stream
            StringBuilder strHeaders = new StringBuilder();
            char c;
            while ((c = (char)stream.read()) != -1 && strHeaders.length() < 50)
            {
                strHeaders.append(c);
                if (strHeaders.length() > 5 && (strHeaders.substring((strHeaders.length() - 4), strHeaders.length()).equals("\r\n\r\n"))) {
                    // end of headers
                    break;
                }
            }

            // Match headers to get metadata offset within a stream
            Pattern p = Pattern.compile("\\r\\n(icy-metaint):\\s*(.*)\\r\\n");
            Matcher m = p.matcher(strHeaders.toString());
            if (m.find())
            {
                metaDataOffset = Integer.parseInt(m.group(2));
            }
        }

        // In case no data was sent
        if (metaDataOffset == 0)
        {
            isError = true;
            return;
        }

        // Read metadata
        int b;
        int count = 0;
        int metaDataLength = 4080; // 4080 is the max length
        boolean inData = false;
        StringBuilder metaData = new StringBuilder();
        // Stream position should be either at the beginning or right after headers
        while ((b = stream.read()) != -1) {
            count++;

            // Length of the metadata
            if (count == metaDataOffset + 1) {
                metaDataLength = b * 16;
            }

            if (count > metaDataOffset + 1 && count < (metaDataOffset + metaDataLength)) {
                inData = true;
            }
            else
            {
                inData = false;
            }
            if (inData)
            {
                if (b != 0)
                {
                    metaData.append((char)b);
                }
            }
            if (count > (metaDataOffset + metaDataLength))
            {
                break;
            }
        }

        // Set the data
        metadata = IcyStreamMeta.parseMetadata(metaData.toString());

        // Close
        stream.close();

    }

    public boolean isError() {
        return isError;
    }

    public URL getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(URL streamUrl) {
        this.metadata = null;
        this.streamUrl = streamUrl;
        this.isError = false;
    }

    public static Map<String, String> parseMetadata(String metaString)
    {
        Map<String, String> metadata = new HashMap();
        String[] metaParts = metaString.split(";");
        Pattern p = Pattern.compile("^([a-zA-Z]+)=\\'([^\\']*)\\'$");
        Matcher m;
        for (int i = 0; i < metaParts.length; i++) {
            m = p.matcher(metaParts[i]);
            if (m.find()) {
                metadata.put((String)m.group(1), (String)m.group(2));
            }
        }

        return metadata;
    }
    public String getStringData() throws IOException{
        StringBuilder sb = new StringBuilder();
        data = getMetadata();
        String[] keyArray = data.keySet().toArray(new String[data.keySet().size()]);
        for(String i : keyArray){
            sb.append(i);
        }
        String string_result = sb.toString();
        return string_result;
    }
}