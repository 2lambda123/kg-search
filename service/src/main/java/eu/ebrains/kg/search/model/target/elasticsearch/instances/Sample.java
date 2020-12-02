package eu.ebrains.kg.search.model.target.elasticsearch.instances;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.ebrains.kg.search.model.target.elasticsearch.ElasticSearchInfo;
import eu.ebrains.kg.search.model.target.elasticsearch.FieldInfo;
import eu.ebrains.kg.search.model.target.elasticsearch.MetaInfo;
import eu.ebrains.kg.search.model.target.elasticsearch.TargetInstance;
import eu.ebrains.kg.search.model.target.elasticsearch.instances.commons.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@MetaInfo(name = "Sample", identifier = "minds/experiment/sample/v1.0.0/search", order = 4)
public class Sample implements TargetInstance {
    @ElasticSearchInfo(mapping = false)
    private Value<String> type = new Value<>("Sample");

    @FieldInfo(visible = false)
    private Value<String> identifier;

    @FieldInfo(label = "Name", sort = true, boost = 20)
    private Value<String> title;

    @FieldInfo(layout = FieldInfo.Layout.HEADER)
    private Value<String> editorId;

    @FieldInfo(label = "Weight pre fixation")
    private Value<String> weightPreFixation;

    @FieldInfo(label = "Brain atlas", layout = FieldInfo.Layout.SUMMARY)
    private List<Value<String>> parcellationAtlas;

    @FieldInfo(label = "Brain region", layout = FieldInfo.Layout.SUMMARY, linkIcon = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 40 40\">\n    <path opacity=\"0.6\" fill=\"#4D4D4D\"\n          d=\"M13.37 33.67a5 5 0 0 1-1.69-.29 4.68 4.68 0 0 1-1.41-.9 4.86 4.86 0 0 0-.94-.66 10.15 10.15 0 0 0-1.07-.42 6.25 6.25 0 0 1-2.05-1 2.33 2.33 0 0 1-.73-2.54 3 3 0 0 0 0-1.29 2.68 2.68 0 0 0-.86-1.13 4.26 4.26 0 0 1-1-1.19 3.88 3.88 0 0 1-.25-2 6.81 6.81 0 0 0 0-.86A4.44 4.44 0 0 0 3 19.91c-.1-.24-.21-.49-.3-.75-.58-1.69-.05-3.45.59-5.26.56-1.58 1.17-3.05 2.54-3.84l.26-.14a1.79 1.79 0 0 0 .53-.36 1.71 1.71 0 0 0 .27-.64C7.32 7.4 8.11 5.35 10 5.27a4.13 4.13 0 0 1 .46 0 1.69 1.69 0 0 0 .55 0 1.71 1.71 0 0 0 .59-.41l.23-.21c.37-.31 2.27-1.81 3.83-1.62a1.81 1.81 0 0 1 1.28.75 2.46 2.46 0 0 1 .29.52 7.37 7.37 0 0 1 .22 3.54c0 .4-.08.79-.09 1.17L17.23 11c0 1.12-.09 2.23-.12 3.35A102.75 102.75 0 0 0 17.22 25c0 .39.08.83.14 1.28.3 2.45.7 5.81-2.17 7a4.79 4.79 0 0 1-1.82.39zm-3.3-27.52H10c-1 0-1.69 1-2.26 3a2.49 2.49 0 0 1-.45 1 2.49 2.49 0 0 1-.8.57l-.23.12c-1.1.63-1.64 2-2.14 3.37-.59 1.65-1.07 3.26-.59 4.67.08.24.18.46.27.69a5.18 5.18 0 0 1 .5 1.71 7.58 7.58 0 0 1 0 1 3.18 3.18 0 0 0 .16 1.59 3.56 3.56 0 0 0 .8.94 3.36 3.36 0 0 1 1.11 1.55 3.76 3.76 0 0 1 0 1.66c-.12.72-.2 1.19.43 1.72a5.51 5.51 0 0 0 1.77.86 10.82 10.82 0 0 1 1.14.4 5.59 5.59 0 0 1 1.11.77 4 4 0 0 0 1.15.75 4.11 4.11 0 0 0 2.88 0c2.25-.93 1.92-3.67 1.63-6.08-.06-.47-.11-.92-.14-1.32a103.52 103.52 0 0 1-.11-10.83c0-1.12.08-2.23.12-3.35L16.42 9c0-.4.06-.82.1-1.22a6.73 6.73 0 0 0-.16-3.12 1.6 1.6 0 0 0-.18-.34.93.93 0 0 0-.68-.4 5.19 5.19 0 0 0-3.15 1.43l-.21.18a2.41 2.41 0 0 1-.93.6 2.39 2.39 0 0 1-.86 0z\"/>\n    <path opacity=\"0.6\" fill=\"#4D4D4D\"\n          d=\"M12.85 11.92a.44.44 0 0 1-.2 0 3.59 3.59 0 0 1-2.13-2.81 6 6 0 0 1 0-.83v-.61a3.36 3.36 0 0 0-1-1.34.44.44 0 0 1 .48-.77 3.84 3.84 0 0 1 1.38 2v.7a5.25 5.25 0 0 0 0 .71A2.73 2.73 0 0 0 13 11.08a.44.44 0 0 1-.2.84zm-3.33 6.17a4.38 4.38 0 0 1-3.77-2.47c-.11-.21-.21-.43-.31-.65a6.21 6.21 0 0 0-.46-.9c-.21-.32-.55-.61-.82-.54a.44.44 0 1 1-.23-.85 1.69 1.69 0 0 1 1.79.9 7 7 0 0 1 .53 1c.09.2.18.41.28.6a3.28 3.28 0 0 0 3.82 1.86.44.44 0 1 1 .28.84 3.49 3.49 0 0 1-1.11.21zm-5.61 4.28a.44.44 0 0 1-.09-.87 3.47 3.47 0 0 0 .63-.89c.49-.84 1.1-1.88 2.12-2a2.5 2.5 0 0 1 2 .95.44.44 0 0 1-.62.62c-.51-.51-.94-.74-1.3-.69-.58.07-1.07.91-1.47 1.58S4.49 22.26 4 22.36zm-.1-.87zm12.43 10.7a.43.43 0 0 1-.37-.21 4 4 0 0 1-.33-1.48c-.1-1-.22-2.08-1-2.51a9.43 9.43 0 0 0-1-.43 6.51 6.51 0 0 1-1.54-.81 3 3 0 0 1-1.05-2 5.53 5.53 0 0 0-.22-.81.43.43 0 0 1 .8-.3 6.28 6.28 0 0 1 .25.93 2.2 2.2 0 0 0 .74 1.49 5.77 5.77 0 0 0 1.36.69 10.33 10.33 0 0 1 1 .47c1.18.64 1.34 2.1 1.45 3.17a3.83 3.83 0 0 0 .22 1.14.43.43 0 0 1-.37.64zm-8.66-3.79c-.33 0-.66 0-1-.06s-.48 0-.68 0a.44.44 0 0 1 0-.88c.23 0 .49 0 .76.05.76.07 1.62.16 2-.32a.44.44 0 0 1 .68.56 2.19 2.19 0 0 1-1.76.65zm3.54-12.48a1.86 1.86 0 0 1-.66-.12.44.44 0 0 1 .31-.83c.65.25 1.41-.32 1.63-.5s.37-.33.55-.5a5.85 5.85 0 0 1 1.25-1 3.22 3.22 0 0 1 2.36-.26.44.44 0 1 1-.24.85 2.33 2.33 0 0 0-1.71.19 5.11 5.11 0 0 0-1.05.85c-.19.19-.39.37-.6.54a3 3 0 0 1-1.84.78zm2.09-6.8a.47.47 0 0 1-.21-.87 12.15 12.15 0 0 0 2.31-1.71 2.89 2.89 0 0 0 .93-2.1.47.47 0 0 1 .94-.09A3.7 3.7 0 0 1 16 7.2a12.85 12.85 0 0 1-2.49 1.86.47.47 0 0 1-.3.07zm-1.79 12.49h-.26a.44.44 0 1 1 0-.88 3.24 3.24 0 0 0 2.5-.65 3.72 3.72 0 0 0 .51-1c.35-.87.84-2.05 2.28-1.81a.44.44 0 0 1-.15.87c-.67-.11-.91.28-1.31 1.27a4.35 4.35 0 0 1-.67 1.24 3.68 3.68 0 0 1-2.9.96zM8 14.23a.44.44 0 0 1-.34-.23 1.05 1.05 0 0 1-.11-.91 1.29 1.29 0 0 1 .84-.72l.3-.1a1.74 1.74 0 0 0 .5-.2.26.26 0 0 0 .12-.19.41.41 0 0 0-.11-.31.44.44 0 1 1 .66-.57 1.29 1.29 0 0 1 .33 1 1.15 1.15 0 0 1-.48.81 2.46 2.46 0 0 1-.76.33l-.25.08c-.22.08-.31.16-.32.2a.28.28 0 0 0 .05.17.44.44 0 0 1-.38.66zm0 11.72h-.15a.44.44 0 0 1-.26-.57 4.2 4.2 0 0 0 .16-1.28A3.85 3.85 0 0 1 8 22.28a.44.44 0 1 1 .77.43 3.2 3.2 0 0 0-.22 1.39 4.89 4.89 0 0 1-.21 1.56.44.44 0 0 1-.34.29z\"/>\n    <path opacity=\"0.6\" fill=\"#4D4D4D\"\n          d=\"M20.51 33.67a4.79 4.79 0 0 1-1.82-.35c-2.87-1.19-2.46-4.55-2.17-7 .06-.46.11-.89.14-1.28a102.83 102.83 0 0 0 .11-10.73c0-1.12-.08-2.23-.12-3.35L16.57 9c0-.38-.05-.77-.09-1.17a7.37 7.37 0 0 1 .22-3.54 2.47 2.47 0 0 1 .3-.51A1.81 1.81 0 0 1 18.26 3c1.55-.2 3.46 1.32 3.83 1.62l.23.21a1.7 1.7 0 0 0 .58.41 1.69 1.69 0 0 0 .55 0 3.93 3.93 0 0 1 .46 0c1.85.08 2.64 2.13 3.07 3.66a1.71 1.71 0 0 0 .27.64 1.79 1.79 0 0 0 .53.36l.26.14c1.36.78 2 2.26 2.54 3.84.64 1.81 1.17 3.57.59 5.26-.09.26-.19.5-.3.75a4.43 4.43 0 0 0-.43 1.42 6.75 6.75 0 0 0 0 .86 3.89 3.89 0 0 1-.25 2 4.26 4.26 0 0 1-1 1.19 2.67 2.67 0 0 0-.86 1.13 3 3 0 0 0 0 1.29 2.33 2.33 0 0 1-.73 2.54 6.24 6.24 0 0 1-2.05 1 10.12 10.12 0 0 0-1.07.42 4.86 4.86 0 0 0-.94.66 4.68 4.68 0 0 1-1.41.9 5 5 0 0 1-1.62.37zM17.69 4.3a1.6 1.6 0 0 0-.18.34 6.74 6.74 0 0 0-.16 3.12c0 .4.08.82.1 1.22l.07 1.95c0 1.12.09 2.23.12 3.35a103.53 103.53 0 0 1-.11 10.83c0 .41-.08.85-.14 1.32-.29 2.41-.62 5.14 1.63 6.08a4.11 4.11 0 0 0 2.88 0 4 4 0 0 0 1.1-.71 5.59 5.59 0 0 1 1.16-.8 10.91 10.91 0 0 1 1.17-.46 5.51 5.51 0 0 0 1.77-.86c.63-.53.55-1 .43-1.72a3.77 3.77 0 0 1 0-1.66 3.36 3.36 0 0 1 1.11-1.55 3.56 3.56 0 0 0 .8-.94 3.18 3.18 0 0 0 .16-1.59 7.63 7.63 0 0 1 0-1 5.18 5.18 0 0 1 .5-1.71c.1-.22.19-.45.27-.69.49-1.42 0-3-.59-4.67-.5-1.41-1-2.73-2.14-3.37l-.23-.12a2.49 2.49 0 0 1-.8-.57 2.49 2.49 0 0 1-.45-1c-.57-2-1.29-3-2.26-3h-.36a2.39 2.39 0 0 1-.86 0 2.4 2.4 0 0 1-.93-.6l-.21-.18a5.19 5.19 0 0 0-3.17-1.41.93.93 0 0 0-.68.4z\"/>\n    <path opacity=\"0.6\" fill=\"#4D4D4D\"\n          d=\"M21 11.92a.44.44 0 0 1-.2-.84A2.73 2.73 0 0 0 22.46 9a5.24 5.24 0 0 0 0-.71v-.7a3.84 3.84 0 0 1 1.38-2 .44.44 0 0 1 .52.72 3.36 3.36 0 0 0-1 1.34v.61a6 6 0 0 1 0 .83 3.59 3.59 0 0 1-2.13 2.81.44.44 0 0 1-.23.02zm3.35 6.17a3.49 3.49 0 0 1-1.12-.18.44.44 0 0 1 .28-.84 3.28 3.28 0 0 0 3.82-1.86c.1-.19.19-.4.28-.6a7 7 0 0 1 .53-1 1.69 1.69 0 0 1 1.79-.9.44.44 0 1 1-.23.85c-.27-.07-.61.21-.82.54a6.25 6.25 0 0 0-.46.91c-.1.22-.2.44-.31.65a4.38 4.38 0 0 1-3.76 2.43zM30 22.37h-.09c-.5-.1-.82-.63-1.21-1.31s-.88-1.5-1.47-1.58c-.36 0-.8.19-1.3.69a.44.44 0 0 1-.62-.62 2.5 2.5 0 0 1 2-.95c1 .13 1.62 1.17 2.12 2a3.39 3.39 0 0 0 .63.89.44.44 0 0 1 .34.52.44.44 0 0 1-.4.36zM17.64 32.2a.43.43 0 0 1-.37-.64 3.83 3.83 0 0 0 .22-1.14c.11-1.07.27-2.54 1.45-3.17a10.29 10.29 0 0 1 1-.47 5.78 5.78 0 0 0 1.36-.69 2.2 2.2 0 0 0 .74-1.49 6.27 6.27 0 0 1 .25-.93.43.43 0 0 1 .8.3 5.56 5.56 0 0 0-.22.81 3 3 0 0 1-1.06 2 6.51 6.51 0 0 1-1.56.81 9.45 9.45 0 0 0-1 .43c-.79.42-.9 1.53-1 2.51A4 4 0 0 1 18 32a.43.43 0 0 1-.36.2zm8.65-3.79a2.19 2.19 0 0 1-1.81-.7.44.44 0 0 1 .68-.56c.39.48 1.25.39 2 .32.27 0 .53-.05.76-.05a.44.44 0 0 1 0 .88h-.68c-.24 0-.62.11-.95.11zm-3.54-12.48a3 3 0 0 1-1.85-.76c-.21-.17-.4-.36-.6-.54a5.11 5.11 0 0 0-1.05-.84 2.32 2.32 0 0 0-1.71-.19.44.44 0 1 1-.24-.85 3.21 3.21 0 0 1 2.36.26 5.85 5.85 0 0 1 1.24 1c.18.17.36.34.55.5s1 .74 1.63.5a.44.44 0 1 1 .31.83 1.85 1.85 0 0 1-.64.09zm-2.09-6.8a.47.47 0 0 1-.25-.07 12.85 12.85 0 0 1-2.49-1.86 3.7 3.7 0 0 1-1.19-2.84.47.47 0 0 1 .51-.43.47.47 0 0 1 .43.51 2.89 2.89 0 0 0 .93 2.1 12.15 12.15 0 0 0 2.31 1.71.47.47 0 0 1-.25.87zm1.8 12.49a3.68 3.68 0 0 1-2.91-.95 4.34 4.34 0 0 1-.67-1.24c-.41-1-.64-1.39-1.31-1.27a.44.44 0 1 1-.15-.87c1.44-.24 1.92.94 2.28 1.81a3.7 3.7 0 0 0 .51 1 3.24 3.24 0 0 0 2.5.65.44.44 0 0 1 0 .88zm3.36-7.39a.44.44 0 0 1-.38-.66.28.28 0 0 0 .05-.17s-.09-.12-.32-.2l-.25-.08a2.46 2.46 0 0 1-.76-.33 1.15 1.15 0 0 1-.48-.81A1.29 1.29 0 0 1 24 11a.44.44 0 1 1 .66.59.41.41 0 0 0-.11.31.26.26 0 0 0 .12.19 1.75 1.75 0 0 0 .5.2l.3.1a1.3 1.3 0 0 1 .84.72 1.05 1.05 0 0 1-.11.91.44.44 0 0 1-.38.21zm.09 11.72a.44.44 0 0 1-.41-.29 4.89 4.89 0 0 1-.21-1.56 3.19 3.19 0 0 0-.22-1.39.44.44 0 1 1 .77-.43 3.84 3.84 0 0 1 .33 1.79 4.2 4.2 0 0 0 .16 1.28.44.44 0 0 1-.26.57z\"/>\n    <path d=\"M13.37 33.67a5 5 0 0 1-1.69-.29 4.68 4.68 0 0 1-1.41-.9 4.86 4.86 0 0 0-.94-.66 10.15 10.15 0 0 0-1.07-.42 6.25 6.25 0 0 1-2.05-1 2.33 2.33 0 0 1-.73-2.54 3 3 0 0 0 0-1.29 2.68 2.68 0 0 0-.86-1.13 4.26 4.26 0 0 1-1-1.19 3.88 3.88 0 0 1-.25-2 6.81 6.81 0 0 0 0-.86A4.44 4.44 0 0 0 3 19.91c-.1-.24-.21-.49-.3-.75-.58-1.69-.05-3.45.59-5.26.56-1.58 1.17-3.05 2.54-3.84l.26-.14a1.79 1.79 0 0 0 .53-.36 1.71 1.71 0 0 0 .27-.64C7.32 7.4 8.11 5.35 10 5.27a4.13 4.13 0 0 1 .46 0 1.69 1.69 0 0 0 .55 0 1.71 1.71 0 0 0 .59-.41l.23-.21c.37-.31 2.27-1.81 3.83-1.62a1.81 1.81 0 0 1 1.28.75 2.46 2.46 0 0 1 .29.52 7.37 7.37 0 0 1 .22 3.54c0 .4-.08.79-.09 1.17L17.23 11c0 1.12-.09 2.23-.12 3.35A102.75 102.75 0 0 0 17.22 25c0 .39.08.83.14 1.28.3 2.45.7 5.81-2.17 7a4.79 4.79 0 0 1-1.82.39zm-3.3-27.52H10c-1 0-1.69 1-2.26 3a2.49 2.49 0 0 1-.45 1 2.49 2.49 0 0 1-.8.57l-.23.12c-1.1.63-1.64 2-2.14 3.37-.59 1.65-1.07 3.26-.59 4.67.08.24.18.46.27.69a5.18 5.18 0 0 1 .5 1.71 7.58 7.58 0 0 1 0 1 3.18 3.18 0 0 0 .16 1.59 3.56 3.56 0 0 0 .8.94 3.36 3.36 0 0 1 1.11 1.55 3.76 3.76 0 0 1 0 1.66c-.12.72-.2 1.19.43 1.72a5.51 5.51 0 0 0 1.77.86 10.82 10.82 0 0 1 1.14.4 5.59 5.59 0 0 1 1.11.77 4 4 0 0 0 1.15.75 4.11 4.11 0 0 0 2.88 0c2.25-.93 1.92-3.67 1.63-6.08-.06-.47-.11-.92-.14-1.32a103.52 103.52 0 0 1-.11-10.83c0-1.12.08-2.23.12-3.35L16.42 9c0-.4.06-.82.1-1.22a6.73 6.73 0 0 0-.16-3.12 1.6 1.6 0 0 0-.18-.34.93.93 0 0 0-.68-.4 5.19 5.19 0 0 0-3.15 1.43l-.21.18a2.41 2.41 0 0 1-.93.6 2.39 2.39 0 0 1-.86 0z\"/>\n    <path d=\"M12.85 11.92a.44.44 0 0 1-.2 0 3.59 3.59 0 0 1-2.13-2.81 6 6 0 0 1 0-.83v-.61a3.36 3.36 0 0 0-1-1.34.44.44 0 0 1 .48-.77 3.84 3.84 0 0 1 1.38 2v.7a5.25 5.25 0 0 0 0 .71A2.73 2.73 0 0 0 13 11.08a.44.44 0 0 1-.2.84zm-3.33 6.17a4.38 4.38 0 0 1-3.77-2.47c-.11-.21-.21-.43-.31-.65a6.21 6.21 0 0 0-.46-.9c-.21-.32-.55-.61-.82-.54a.44.44 0 1 1-.23-.85 1.69 1.69 0 0 1 1.79.9 7 7 0 0 1 .53 1c.09.2.18.41.28.6a3.28 3.28 0 0 0 3.82 1.86.44.44 0 1 1 .28.84 3.49 3.49 0 0 1-1.11.21zm-5.61 4.28a.44.44 0 0 1-.09-.87 3.47 3.47 0 0 0 .63-.89c.49-.84 1.1-1.88 2.12-2a2.5 2.5 0 0 1 2 .95.44.44 0 0 1-.62.62c-.51-.51-.94-.74-1.3-.69-.58.07-1.07.91-1.47 1.58S4.49 22.26 4 22.36zm-.1-.87zm12.43 10.7a.43.43 0 0 1-.37-.21 4 4 0 0 1-.33-1.48c-.1-1-.22-2.08-1-2.51a9.43 9.43 0 0 0-1-.43 6.51 6.51 0 0 1-1.54-.81 3 3 0 0 1-1.05-2 5.53 5.53 0 0 0-.22-.81.43.43 0 0 1 .8-.3 6.28 6.28 0 0 1 .25.93 2.2 2.2 0 0 0 .74 1.49 5.77 5.77 0 0 0 1.36.69 10.33 10.33 0 0 1 1 .47c1.18.64 1.34 2.1 1.45 3.17a3.83 3.83 0 0 0 .22 1.14.43.43 0 0 1-.37.64zm-8.66-3.79c-.33 0-.66 0-1-.06s-.48 0-.68 0a.44.44 0 0 1 0-.88c.23 0 .49 0 .76.05.76.07 1.62.16 2-.32a.44.44 0 0 1 .68.56 2.19 2.19 0 0 1-1.76.65zm3.54-12.48a1.86 1.86 0 0 1-.66-.12.44.44 0 0 1 .31-.83c.65.25 1.41-.32 1.63-.5s.37-.33.55-.5a5.85 5.85 0 0 1 1.25-1 3.22 3.22 0 0 1 2.36-.26.44.44 0 1 1-.24.85 2.33 2.33 0 0 0-1.71.19 5.11 5.11 0 0 0-1.05.85c-.19.19-.39.37-.6.54a3 3 0 0 1-1.84.78zm2.09-6.8a.47.47 0 0 1-.21-.87 12.15 12.15 0 0 0 2.31-1.71 2.89 2.89 0 0 0 .93-2.1.47.47 0 0 1 .94-.09A3.7 3.7 0 0 1 16 7.2a12.85 12.85 0 0 1-2.49 1.86.47.47 0 0 1-.3.07zm-1.79 12.49h-.26a.44.44 0 1 1 0-.88 3.24 3.24 0 0 0 2.5-.65 3.72 3.72 0 0 0 .51-1c.35-.87.84-2.05 2.28-1.81a.44.44 0 0 1-.15.87c-.67-.11-.91.28-1.31 1.27a4.35 4.35 0 0 1-.67 1.24 3.68 3.68 0 0 1-2.9.96zM8 14.23a.44.44 0 0 1-.34-.23 1.05 1.05 0 0 1-.11-.91 1.29 1.29 0 0 1 .84-.72l.3-.1a1.74 1.74 0 0 0 .5-.2.26.26 0 0 0 .12-.19.41.41 0 0 0-.11-.31.44.44 0 1 1 .66-.57 1.29 1.29 0 0 1 .33 1 1.15 1.15 0 0 1-.48.81 2.46 2.46 0 0 1-.76.33l-.25.08c-.22.08-.31.16-.32.2a.28.28 0 0 0 .05.17.44.44 0 0 1-.38.66zm0 11.72h-.15a.44.44 0 0 1-.26-.57 4.2 4.2 0 0 0 .16-1.28A3.85 3.85 0 0 1 8 22.28a.44.44 0 1 1 .77.43 3.2 3.2 0 0 0-.22 1.39 4.89 4.89 0 0 1-.21 1.56.44.44 0 0 1-.34.29z\"/>\n    <path d=\"M20.51 33.67a4.79 4.79 0 0 1-1.82-.35c-2.87-1.19-2.46-4.55-2.17-7 .06-.46.11-.89.14-1.28a102.83 102.83 0 0 0 .11-10.73c0-1.12-.08-2.23-.12-3.35L16.57 9c0-.38-.05-.77-.09-1.17a7.37 7.37 0 0 1 .22-3.54 2.47 2.47 0 0 1 .3-.51A1.81 1.81 0 0 1 18.26 3c1.55-.2 3.46 1.32 3.83 1.62l.23.21a1.7 1.7 0 0 0 .58.41 1.69 1.69 0 0 0 .55 0 3.93 3.93 0 0 1 .46 0c1.85.08 2.64 2.13 3.07 3.66a1.71 1.71 0 0 0 .27.64 1.79 1.79 0 0 0 .53.36l.26.14c1.36.78 2 2.26 2.54 3.84.64 1.81 1.17 3.57.59 5.26-.09.26-.19.5-.3.75a4.43 4.43 0 0 0-.43 1.42 6.75 6.75 0 0 0 0 .86 3.89 3.89 0 0 1-.25 2 4.26 4.26 0 0 1-1 1.19 2.67 2.67 0 0 0-.86 1.13 3 3 0 0 0 0 1.29 2.33 2.33 0 0 1-.73 2.54 6.24 6.24 0 0 1-2.05 1 10.12 10.12 0 0 0-1.07.42 4.86 4.86 0 0 0-.94.66 4.68 4.68 0 0 1-1.41.9 5 5 0 0 1-1.62.37zM17.69 4.3a1.6 1.6 0 0 0-.18.34 6.74 6.74 0 0 0-.16 3.12c0 .4.08.82.1 1.22l.07 1.95c0 1.12.09 2.23.12 3.35a103.53 103.53 0 0 1-.11 10.83c0 .41-.08.85-.14 1.32-.29 2.41-.62 5.14 1.63 6.08a4.11 4.11 0 0 0 2.88 0 4 4 0 0 0 1.1-.71 5.59 5.59 0 0 1 1.16-.8 10.91 10.91 0 0 1 1.17-.46 5.51 5.51 0 0 0 1.77-.86c.63-.53.55-1 .43-1.72a3.77 3.77 0 0 1 0-1.66 3.36 3.36 0 0 1 1.11-1.55 3.56 3.56 0 0 0 .8-.94 3.18 3.18 0 0 0 .16-1.59 7.63 7.63 0 0 1 0-1 5.18 5.18 0 0 1 .5-1.71c.1-.22.19-.45.27-.69.49-1.42 0-3-.59-4.67-.5-1.41-1-2.73-2.14-3.37l-.23-.12a2.49 2.49 0 0 1-.8-.57 2.49 2.49 0 0 1-.45-1c-.57-2-1.29-3-2.26-3h-.36a2.39 2.39 0 0 1-.86 0 2.4 2.4 0 0 1-.93-.6l-.21-.18a5.19 5.19 0 0 0-3.17-1.41.93.93 0 0 0-.68.4z\"/>\n    <path d=\"M21 11.92a.44.44 0 0 1-.2-.84A2.73 2.73 0 0 0 22.46 9a5.24 5.24 0 0 0 0-.71v-.7a3.84 3.84 0 0 1 1.38-2 .44.44 0 0 1 .52.72 3.36 3.36 0 0 0-1 1.34v.61a6 6 0 0 1 0 .83 3.59 3.59 0 0 1-2.13 2.81.44.44 0 0 1-.23.02zm3.35 6.17a3.49 3.49 0 0 1-1.12-.18.44.44 0 0 1 .28-.84 3.28 3.28 0 0 0 3.82-1.86c.1-.19.19-.4.28-.6a7 7 0 0 1 .53-1 1.69 1.69 0 0 1 1.79-.9.44.44 0 1 1-.23.85c-.27-.07-.61.21-.82.54a6.25 6.25 0 0 0-.46.91c-.1.22-.2.44-.31.65a4.38 4.38 0 0 1-3.76 2.43zM30 22.37h-.09c-.5-.1-.82-.63-1.21-1.31s-.88-1.5-1.47-1.58c-.36 0-.8.19-1.3.69a.44.44 0 0 1-.62-.62 2.5 2.5 0 0 1 2-.95c1 .13 1.62 1.17 2.12 2a3.39 3.39 0 0 0 .63.89.44.44 0 0 1 .34.52.44.44 0 0 1-.4.36zM17.64 32.2a.43.43 0 0 1-.37-.64 3.83 3.83 0 0 0 .22-1.14c.11-1.07.27-2.54 1.45-3.17a10.29 10.29 0 0 1 1-.47 5.78 5.78 0 0 0 1.36-.69 2.2 2.2 0 0 0 .74-1.49 6.27 6.27 0 0 1 .25-.93.43.43 0 0 1 .8.3 5.56 5.56 0 0 0-.22.81 3 3 0 0 1-1.06 2 6.51 6.51 0 0 1-1.56.81 9.45 9.45 0 0 0-1 .43c-.79.42-.9 1.53-1 2.51A4 4 0 0 1 18 32a.43.43 0 0 1-.36.2zm8.65-3.79a2.19 2.19 0 0 1-1.81-.7.44.44 0 0 1 .68-.56c.39.48 1.25.39 2 .32.27 0 .53-.05.76-.05a.44.44 0 0 1 0 .88h-.68c-.24 0-.62.11-.95.11zm-3.54-12.48a3 3 0 0 1-1.85-.76c-.21-.17-.4-.36-.6-.54a5.11 5.11 0 0 0-1.05-.84 2.32 2.32 0 0 0-1.71-.19.44.44 0 1 1-.24-.85 3.21 3.21 0 0 1 2.36.26 5.85 5.85 0 0 1 1.24 1c.18.17.36.34.55.5s1 .74 1.63.5a.44.44 0 1 1 .31.83 1.85 1.85 0 0 1-.64.09zm-2.09-6.8a.47.47 0 0 1-.25-.07 12.85 12.85 0 0 1-2.49-1.86 3.7 3.7 0 0 1-1.19-2.84.47.47 0 0 1 .51-.43.47.47 0 0 1 .43.51 2.89 2.89 0 0 0 .93 2.1 12.15 12.15 0 0 0 2.31 1.71.47.47 0 0 1-.25.87zm1.8 12.49a3.68 3.68 0 0 1-2.91-.95 4.34 4.34 0 0 1-.67-1.24c-.41-1-.64-1.39-1.31-1.27a.44.44 0 1 1-.15-.87c1.44-.24 1.92.94 2.28 1.81a3.7 3.7 0 0 0 .51 1 3.24 3.24 0 0 0 2.5.65.44.44 0 0 1 0 .88zm3.36-7.39a.44.44 0 0 1-.38-.66.28.28 0 0 0 .05-.17s-.09-.12-.32-.2l-.25-.08a2.46 2.46 0 0 1-.76-.33 1.15 1.15 0 0 1-.48-.81A1.29 1.29 0 0 1 24 11a.44.44 0 1 1 .66.59.41.41 0 0 0-.11.31.26.26 0 0 0 .12.19 1.75 1.75 0 0 0 .5.2l.3.1a1.3 1.3 0 0 1 .84.72 1.05 1.05 0 0 1-.11.91.44.44 0 0 1-.38.21zm.09 11.72a.44.44 0 0 1-.41-.29 4.89 4.89 0 0 1-.21-1.56 3.19 3.19 0 0 0-.22-1.39.44.44 0 1 1 .77-.43 3.84 3.84 0 0 1 .33 1.79 4.2 4.2 0 0 0 .16 1.28.44.44 0 0 1-.26.57z\"/>\n    <path  d=\"M36.68 34.83l-5.63-5.19a7.19 7.19 0 1 0-1.17 1.59l5.47 5a1 1 0 0 0 1.33-1.44zm-11.92-3a5.56 5.56 0 1 1 5.56-5.56 5.56 5.56 0 0 1-5.56 5.55z\"/>\n</svg>")
    private List<TargetExternalReference> region;

    @FieldInfo(label = "Viewer")
    private List<TargetExternalReference> viewer;

    @FieldInfo(label = "Methods", overview = true, overviewMaxDisplay = 1, layout = FieldInfo.Layout.SUMMARY, tagIcon = "<svg width=\"50\" height=\"50\" viewBox=\"0 0 11.377083 13.05244\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M 5.6585847,-3.1036376e-7 2.8334327,1.5730297 0.0088,3.1455497 0.0047,6.4719597 0,9.7983697 2.8323857,11.42515 l 2.831867,1.62729 1.070218,-0.60358 c 0.588756,-0.33201 1.874409,-1.06813 2.856675,-1.63608 L 11.377083,9.7797697 v -3.24735 -3.24786 l -0.992187,-0.62477 C 9.8391917,2.3160397 8.5525477,1.5769697 7.5256387,1.0175097 Z M 5.6580697,3.7398297 a 2.7061041,2.7144562 0 0 1 2.706293,2.71456 2.7061041,2.7144562 0 0 1 -2.706293,2.71456 2.7061041,2.7144562 0 0 1 -2.70578,-2.71456 2.7061041,2.7144562 0 0 1 2.70578,-2.71456 z\"/></svg>")
    private List<Value<String>> methods;

    @JsonProperty("allfiles") //TODO: capitalize
    @FieldInfo(label = "Data download", isButton = true, termsOfUse = true)
    private TargetExternalReference allFiles;

    @FieldInfo(label = "Files", layout = FieldInfo.Layout.GROUP, termsOfUse = true)
    private List<TargetFile> files;

    @FieldInfo(label = "Subject", overview = true, hint = "Experimental subject that this sample was obtained from.")
    private List<Children<Subject>> subject;

    @FieldInfo(label = "Datasets", visible = false, facet = FieldInfo.Facet.EXISTS)
    private List<Value<String>> datasetExists;

    @FieldInfo(label = "Datasets", layout = FieldInfo.Layout.GROUP, type = FieldInfo.Type.TEXT, hint = "List of datasets in which the sample was used to produce data.")
    private List<Children<Dataset>> datasets;

    @JsonProperty("first_release")
    @FieldInfo(label = "First release", ignoreForSearch = true, visible = false, type = FieldInfo.Type.DATE)
    private ISODateValue firstRelease;

    @JsonProperty("last_release")
    @FieldInfo(label = "Last release", ignoreForSearch = true, visible = false, type = FieldInfo.Type.DATE)
    private ISODateValue lastRelease;

    public void setType(String type) {
        setType(StringUtils.isBlank(type) ? null : new Value<>(type));
    }

    public void setIdentifier(String identifier) {
        setIdentifier(StringUtils.isBlank(identifier) ? null : new Value<>(identifier));
    }

    public void setEditorId(String editorId) {
        setEditorId(StringUtils.isBlank(editorId) ? null : new Value<>(editorId));
    }

    public void setTitle(String title) {
        setTitle(StringUtils.isBlank(title) ? null : new Value<>(title));
    }

    public Value<String> getType() {
        return type;
    }

    public void setType(Value<String> type) {
        this.type = type;
    }

    @Override
    public Value<String> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Value<String> identifier) {
        this.identifier = identifier;
    }

    public Value<String> getTitle() {
        return title;
    }

    public void setTitle(Value<String> title) {
        this.title = title;
    }

    public Value<String> getEditorId() {
        return editorId;
    }

    public void setEditorId(Value<String> editorId) {
        this.editorId = editorId;
    }

    public Value<String> getWeightPreFixation() {
        return weightPreFixation;
    }

    public void setWeightPreFixation(String weightPreFixation) {
        setWeightPreFixation(StringUtils.isBlank(weightPreFixation) ? null : new Value<>(weightPreFixation));
    }

    public void setWeightPreFixation(Value<String> weightPreFixation) {
        this.weightPreFixation = weightPreFixation;
    }

    public List<Value<String>> getParcellationAtlas() {
        return parcellationAtlas;
    }

    public void setParcellationAtlas(List<String> parcellationAtlas) {
        this.parcellationAtlas = parcellationAtlas == null ? null : parcellationAtlas.stream().map(Value::new).collect(Collectors.toList());
    }

    public List<TargetExternalReference> getRegion() {
        return region;
    }

    public void setRegion(List<TargetExternalReference> region) {
        this.region = region;
    }

    public List<TargetExternalReference> getViewer() {
        return viewer;
    }

    public void setViewer(List<TargetExternalReference> viewer) {
        this.viewer = viewer;
    }

    public List<Value<String>> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods == null ? null : methods.stream().map(Value::new).collect(Collectors.toList());
    }

    public TargetExternalReference getAllFiles() {
        return allFiles;
    }

    public void setAllFiles(TargetExternalReference allFiles) {
        this.allFiles = allFiles;
    }

    public List<TargetFile> getFiles() {
        return files;
    }

    public void setFiles(List<TargetFile> files) {
        this.files =  files;
    }

    public List<Children<Subject>> getSubject() {
        return subject;
    }

    public void setSubject(List<Subject> subject) {
        this.subject = subject != null ? subject.stream().map(Children::new).collect(Collectors.toList()) : null;
    }

    public List<Value<String>> getDatasetExists() {
        return datasetExists;
    }

    public void setDatasetExists(List<String> datasetExists) {
        this.datasetExists = datasetExists != null ? datasetExists.stream().map(Value::new).collect(Collectors.toList()) : null;
    }

    public List<Children<Dataset>> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets != null ? datasets.stream().map(Children::new).collect(Collectors.toList()) : null;
    }

    public ISODateValue getFirstRelease() {
        return firstRelease;
    }

    public void setFirstRelease(ISODateValue firstRelease) {
        this.firstRelease = firstRelease;
    }

    public void setFirstRelease(Date firstRelease) {
        this.setFirstRelease(firstRelease != null ? new ISODateValue(firstRelease) : null);
    }

    public ISODateValue getLastRelease() {
        return lastRelease;
    }

    public void setLastRelease(ISODateValue lastRelease) {
        this.lastRelease = lastRelease;
    }

    public void setLastRelease(Date lastRelease) {
        this.setLastRelease(lastRelease != null ? new ISODateValue(lastRelease) : null);
    }

    public static class Dataset {
        public Dataset() {
        }

        public Dataset(List<String> component, List<TargetInternalReference> name) {
            this.component = component == null  ? null : component.stream().map(Value::new).collect(Collectors.toList());
            this.name = name;
        }

        @FieldInfo(type = FieldInfo.Type.TEXT)
        private List<Value<String>> component;

        private List<TargetInternalReference> name;

        public List<Value<String>> getComponent() {
            return component;
        }

        public void setComponent(List<Value<String>> component) {
            this.component = component;
        }

        public List<TargetInternalReference> getName() {
            return name;
        }

        public void setName(List<TargetInternalReference> name) {
            this.name = name;
        }
    }

    public static class Subject {
        public Subject() {
        }

        public Subject(TargetInternalReference subjectName,
                       List<String> species,
                       List<String> sex,
                       String age,
                       List<String> ageCategory,
                       String weight,
                       String strain,
                       String genotype) {
            this.subjectName = subjectName;
            this.species = species != null ? species.stream().map(Value::new).collect(Collectors.toList()) : null;
            this.sex = sex != null ? sex.stream().map(Value::new).collect(Collectors.toList()) : null;
            this.age = StringUtils.isBlank(age) ? null : new Value<>(age);
            this.ageCategory = ageCategory != null ? ageCategory.stream().map(Value::new).collect(Collectors.toList()) : null;
            this.weight = StringUtils.isBlank(weight) ? null : new Value<>(weight);
            this.strain = StringUtils.isBlank(strain) ? null : new Value<>(strain);
            this.genotype = StringUtils.isBlank(genotype) ? null : new Value<>(genotype);
        }

        @JsonProperty("subject_name")
        @FieldInfo(label = "Name")
        private TargetInternalReference subjectName;

        @FieldInfo(label = "Species", facet = FieldInfo.Facet.LIST, type = FieldInfo.Type.TEXT, overview = true, facetOrder = FieldInfo.FacetOrder.BYCOUNT)
        private List<Value<String>> species;

        @FieldInfo(label = "Sex", facet = FieldInfo.Facet.LIST)
        private List<Value<String>> sex;

        @FieldInfo(label = "Age")
        private Value<String> age;

        @JsonProperty("agecategory") //TODO: change this to ageCategory
        @FieldInfo(label = "Age category")
        private List<Value<String>> ageCategory;

        @FieldInfo(label = "Weight")
        private Value<String> weight;

        @FieldInfo(label = "Strain")
        private Value<String> strain;

        @FieldInfo(label = "Genotype")
        private Value<String> genotype;

        public TargetInternalReference getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(TargetInternalReference subjectName) {
            this.subjectName = subjectName;
        }

        public List<Value<String>> getSpecies() {
            return species;
        }

        public void setSpecies(List<Value<String>> species) {
            this.species = species;
        }

        public List<Value<String>> getSex() {
            return sex;
        }

        public void setSex(List<Value<String>> sex) {
            this.sex = sex;
        }

        public Value<String> getAge() {
            return age;
        }

        public void setAge(Value<String> age) {
            this.age = age;
        }

        public List<Value<String>> getAgeCategory() {
            return ageCategory;
        }

        public void setAgeCategory(List<Value<String>> ageCategory) {
            this.ageCategory = ageCategory;
        }

        public Value<String> getWeight() {
            return weight;
        }

        public void setWeight(Value<String> weight) {
            this.weight = weight;
        }

        public Value<String> getStrain() {
            return strain;
        }

        public void setStrain(Value<String> strain) {
            this.strain = strain;
        }

        public Value<String> getGenotype() {
            return genotype;
        }

        public void setGenotype(Value<String> genotype) {
            this.genotype = genotype;
        }
    }
}
