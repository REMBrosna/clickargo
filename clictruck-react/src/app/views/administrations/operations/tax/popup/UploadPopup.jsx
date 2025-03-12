import React, { useState } from "react";
import { FilePond } from 'react-filepond';
import 'filepond/dist/filepond.min.css'; 
import { useEffect } from "react";
import PropTypes from 'prop-types';

const UploadPopup = (props) => {

    const {getFileUpload, setUploadData } = props;

    const [files, setFiles] = useState([]);
    const [pond, setPond] =  useState("");

    const handleInit = () => {
        console.log('FilePond instance has initialised', pond);
    }

    const handleRemoveFile = (i)=>{
        console.log("FilePond test remove", `upload ke ${i}`)   
        // pond.removeFile(0)
    }

    return (
        <>
            <FilePond
                ref={(ref) => setUploadData(ref)}
                files={files}
                allowMultiple={true}
                maxFiles={5000}
                oninit={handleInit}
                // onremovefile={handleRemoveFile}
                instantUpload={false}
                onupdatefiles={(fileItems) => {
                    const items = fileItems.map((fileItem) => {
                       return {
                            file: fileItem?.file,
                            fileExtension: fileItem?.fileExtension,
                            fileSize: fileItem?.fileSize,
                            fileType: fileItem?.fileType,
                            filename: fileItem?.filename,
                            filenameWithoutExtension: fileItem?.filenameWithoutExtension
                        }
                    });
                    getFileUpload(items);

                }}
            />
        </>
    )
}

UploadPopup.propTypes = {
    getFileUpload: PropTypes.func,
    setUploadData: PropTypes.func
} 

export default UploadPopup;