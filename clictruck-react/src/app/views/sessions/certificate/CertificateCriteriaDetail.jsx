import React, {useEffect, useState} from 'react';
import useHttp from "../../../c1hooks/http";
import {useParams} from "react-router-dom";
import SearchCriteriaForm from "./SearchCriteriaForm";
import {useTranslation} from "react-i18next";

const CertificateCriteriaDetail = () => {

    const { type, certToken } = useParams();
    const [certificate, setCertificate] = useState();
    const { isLoading, res, error, urlId, sendRequest } = useHttp();
    const [isAlive, setIsAlive] = useState(true);
    const defaultCriteria = {certificateNo: "", imoNo: "", vesselName: "", certificateType: ""}
    const [searchCriteria, setSearchCriteria] = useState(defaultCriteria);
    const [errorCriteria, setErrorCriteria] = useState({});
    const { t } = useTranslation(["certificate"]);
    const [isInquiry, setInquiry] = useState(false);

    useEffect(() => {
        if (type === 'view') {
            sendRequest("/api/certificate/print/redirect/" + certToken, "printCertificate", "POST");
        }
        return () => setIsAlive(false);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isAlive, certToken, type]);

    useEffect(()=> {
        if (!isLoading && !error && res) {
            switch (urlId) {
                case "printCertificate":
                case "searchCertificate":
                    setCertificate(res.data);
                    setInquiry(false);
                    break;
                default:
                    break;
            }
        }else{
            setInquiry(true);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    },[isLoading, res, error, urlId])

    const onCertificateNoChangeHandler = (e)=>{
        const temp ={...searchCriteria,
            certificateNo: e.target.value
        };
        setSearchCriteria(temp);
    }
    const onImoNoChangeHandler = (e) =>{
        const temp ={...searchCriteria,
            imoNo: e.target.value
        };
        setSearchCriteria(temp);
    }

    const onVesselNameChangeHandler = (e) => {
        const temp ={...searchCriteria,
            vesselName: e.target.value
        };
        setSearchCriteria(temp);
    }

    const selectChangeHandler = (e) =>{
        const temp ={...searchCriteria,
            certificateType: e.target.value
        };
        setSearchCriteria(temp);
    }

    const onSearchClickButton = () => {
        setErrorCriteria({});
        const {certificateNo, certificateType} = searchCriteria;
        if (certificateNo === '' || certificateType === ''){
            setErrorCriteria({
                certificateNo: certificateNo === '' && t("certificate:certificate.message.required"),
                certificateType: certificateType === '' && t("certificate:certificate.message.required")
            });
        } else {
            sendRequest("/api/certificate/print/searchCertificate", "searchCertificate", "POST", searchCriteria);
        }
    }
    return (
        <React.Fragment>
            <SearchCriteriaForm
            certificateNoChangeHandle={onCertificateNoChangeHandler}
            imoChangeHandle={onImoNoChangeHandler}
            onSearchHandle={onSearchClickButton}
            selectChange={selectChangeHandler}
            vesselNameChangeHandle={onVesselNameChangeHandler}
            certificateData={certificate}
            errors={errorCriteria}
            isInquiry={isInquiry}
            />
        </React.Fragment>
    );
}
export default CertificateCriteriaDetail;