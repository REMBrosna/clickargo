import React, { useState, useEffect } from "react";
import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css';
import WorkBenchDocumentCard from "./WorkBenchDocumentCard";
import Grid from "@material-ui/core/Grid";
import PropTypes from 'prop-types';
import useAuth from "app/hooks/useAuth";
import * as WorkflowUtil from "./WorkflowUtil"
import { Status } from "app/c1utils/const";
import { isArrayNotEmpty } from "app/c1utils/utility";
import withErrorHandler from "app/hoc/withErrorHandler/withErrorHandler";

const WorkBench = ({ docs }) => {

    const workBenchSelectedId = "workBenchSelectedId";
    const [docFiles, setDocFiles] = useState([]);
    const [selectedId, setSelectedId] = useState(1);
    const [filterStatus, setFilterStatus] = useState([]);
    const [openStatus, setOpenStatus] = useState(false);
    const { user } = useAuth();

    useEffect(() => {

        let docsExt = docs && docs.map(d => {
            //setDocFiles(f => ([...f, { state: 'close', img: `${d.docType}_x.png`, ...d }]));
            return { ...d, state: 'close', img: `${d.docType}_x.png` }
        });
        setDocFiles(docsExt);

        // initial
        let storagedId = sessionStorage.getItem(workBenchSelectedId);

        if (docsExt && docsExt.length > 0) {
            if (docsExt && storagedId) {
                setSelectedIdWrapp(storagedId);
            } else {
                setSelectedIdWrapp(docsExt[0].id);
            }
        }
        return () => { };

    }, [docs]);

    const setSelectedIdWrapp = (id) => {
        setDocFiles(dcF => dcF.map(df => {
            let nwDf = {};
            if (df.id === id) {
                Object.assign(nwDf, df, { state: 'open', img: `${df.docType}_o.png` });
            } else {
                Object.assign(nwDf, df, { state: 'close', img: `${df.docType}_x.png` });
            }
            return nwDf;
        }));

        setSelectedId(id);
    }
    const toggleImageShowListing = (id) => {
        // console.log("toggleImageShowListing", id);
        setSelectedIdWrapp(id);
        sessionStorage.setItem(workBenchSelectedId, id);
        setFilterStatus([]);
        setOpenStatus(false);
    }

    const handleClickStatus = (e, docObjId, filterStatusParam) => {
        e.stopPropagation(); // important, can't trigger toggleImageShowListing function;
        if ("Open" !== filterStatusParam) {
            setFilterStatus([filterStatusParam]);
            setOpenStatus(true);
        } else {
            setFilterStatus();
            setOpenStatus(false);
        }
        setSelectedIdWrapp(docObjId);
        sessionStorage.setItem(workBenchSelectedId, docObjId);
    }

    const handleFilterChipClose = (index, removedFilter, filterList) => {
        let indx = filterStatus.indexOf(removedFilter);
        filterStatus.splice(indx, 1);
        setFilterStatus(filterStatus);
    }

    const handleFilterChange = (changedColumn, filterList, type, changedColumnIndex, displayData) => {
        if (type === 'reset')
            setFilterStatus([]);
        else {
            //PORTEDI-1566 to append the status selected from workbench card, and at the same time to also not include
            //the status when the workbench card folder is clicked instead of status filtering - which causes an extra blank chip
            if (changedColumn && (changedColumn.includes('status') || changedColumn.includes('Status'))
                && filterStatus && isArrayNotEmpty(filterStatus))
                // filterStatusArr.push(filterList[changedColumnIndex]);
                setFilterStatus([...filterStatus, filterList[changedColumnIndex]]);
        }

        if (changedColumn === 'pediApps.pediMstAppStatus.appStatusId') {
            setFilterStatus(filterList[changedColumnIndex]);
        }
    }

    const convertStatus = (status) => {
        if (status === Status.SUB.code) {
            return [WorkflowUtil.WorkflowStage.PendingApproval, WorkflowUtil.WorkflowStage.PendingVerification];
        } else {
            return [WorkflowUtil.WorkflowStage.PendingVerification];
        }
        // if (WorkflowUtil.WorkflowStage.PendingApproval === status) {
        //     return [WorkflowUtil.WorkflowStage.PendingApproval, WorkflowUtil.WorkflowStage.PendingVerification];
        // } else {
        //     return [status]
        // }
    }

    return (
        <div className="m-sm-30">
            <Grid container spacing={3} direction="row" justify="center" alignItems="center">
                <Grid item xs={12}>
                    <Carousel
                        additionalTransfrom={0}
                        arrows
                        autoPlaySpeed={3000}
                        centerMode={false}
                        className=""
                        containerClass="first-carousel-container container"
                        dotListClass=""
                        draggable
                        focusOnSelect={false}
                        itemClass=""
                        keyBoardControl
                        minimumTouchDrag={80}
                        renderButtonGroupOutside={false}
                        renderDotsOutside={false}
                        responsive={{
                            desktop: {
                                breakpoint: {
                                    max: 3000,
                                    min: 1024
                                },
                                items: 5,
                                partialVisibilityGutter: 40
                            },
                            mobile: {
                                breakpoint: {
                                    max: 464,
                                    min: 0
                                },
                                items: 1,
                                partialVisibilityGutter: 30
                            },
                            tablet: {
                                breakpoint: {
                                    max: 1024,
                                    min: 464
                                },
                                items: 2,
                                partialVisibilityGutter: 30
                            }
                        }}
                        showDots={false}
                        sliderClass=""
                        slidesToSlide={1}
                        swipeable>
                        {docFiles && docFiles.map(file => {
                            let displayEl = null;
                            if (file && file.id !== '') {
                                displayEl = <WorkBenchDocumentCard
                                    docObj={file}
                                    imagePath="doctypesPort"
                                    key={file.id}
                                    toggleEventHandler={() => toggleImageShowListing(file.id)}
                                    handleClickStatus={handleClickStatus}>
                                </WorkBenchDocumentCard>;
                            }
                            return displayEl;
                        })}

                    </Carousel>
                </Grid>
                <Grid item xs={12}>
                    {docFiles && docFiles.map(file => {
                        if (file && file.id === selectedId) {
                            return file.listComponent({ roleId: user.role, filterStatus, setFilterStatus, onFilterChipClose: handleFilterChipClose, onFilterChange: handleFilterChange });
                        }

                        return null;
                    })}

                </Grid>
            </Grid>
        </div>
    );

};

WorkBench.propTypes = {
    docs: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.string,
            docType: PropTypes.string,
            status: PropTypes.number,
            statusLabel: PropTypes.string,
            title: PropTypes.string,
            uriPathNewApp: PropTypes.string

        }))
}

export default withErrorHandler(WorkBench);