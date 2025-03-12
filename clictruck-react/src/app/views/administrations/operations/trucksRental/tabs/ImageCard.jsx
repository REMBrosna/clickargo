import React, {useEffect, useState} from "react";
import CardMedia from "@material-ui/core/CardMedia";
import useHttp from "../../../../../c1hooks/http";
import Skeleton from "@material-ui/lab/Skeleton";

const ImageCards = (props) => {

    const {
        fileName,
        currentImg,
        imageSource,
        currentIndex,
        setImageSource
    } = props;

    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        if (fileName === currentImg?.picture){
            sendRequest(`/api/v1/clickargo/clictruck/administrator/truck/image/${currentImg?.providerCode}/${btoa(currentImg?.picture)}`, "GET_IMAGE");
            setLoading(true)
        }
    }, [sendRequest, currentIndex, fileName]);

    useEffect(() => {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "GET_IMAGE":
                    setImageSource(`data:image/jpeg;base64,${res.data}`);
                    setLoading(false)
                    break;
                default:
                    break;
            }
        }
    }, [isLoading, error, res, urlId, imageSource]);

    return (
        <>
            {loading ? (
                <Skeleton variant="rect" width="100%">
                    <div style={{ paddingTop: '57%' }} />
                </Skeleton>
            ) : (
                <img src={imageSource}
                     style={{
                        maxWidth: "100%",
                        maxHeight: "100%",
                        margin: "40px"
                    }}
                />
            )}
        </>
    )
}

export default ImageCards;