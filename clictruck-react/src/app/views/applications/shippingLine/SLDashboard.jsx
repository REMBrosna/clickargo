import React, { useState, useEffect } from "react";
import Carousel from 'react-multi-carousel';
import Grid from "@material-ui/core/Grid";
import useAuth from "app/hooks/useAuth";
import PropTypes from 'prop-types';
import { isArrayNotEmpty } from "app/c1utils/utility";
import SLDashboardCard from "./SLDashboardCard";

const SLDashboard = ({ docs }) => {

    const workBenchSelectedId = "workBenchSelectedId";

    const [docFiles, setDocFiles] = useState(//[]
        docs && docs.map(d => {
            return { ...d }
        })
    );
    const [selectedId, setSelectedId] = useState(1);
    const [filterStatus, setFilterStatus] = useState([]);
    const [openStatus, setOpenStatus] = useState(false);
    const { user } = useAuth();

    const toggleImageShowListing = (id) => {
        // console.log("toggleImageShowListing", id);
        setSelectedIdWrapp(id);
        sessionStorage.setItem(workBenchSelectedId, id);
        setFilterStatus([]);
        setOpenStatus(false);
    }

    useEffect(() => {

        let docsExt = docs && docs.map(d => {
            setDocFiles(f => ([...f, { ...d }]));
            return { ...d }
        });
        setDocFiles(docsExt);

        //initial
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
                Object.assign(nwDf, df, { state: 'active' });
            } else {
                Object.assign(nwDf, df, { state: 'inactive' });
            }
            return nwDf;
        }));

        setSelectedId(id);
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
                setFilterStatus([...filterStatus, filterList[changedColumnIndex]]);
        }

        // if (changedColumn === 'pediApps.pediMstAppStatus.appStatusId') {
        //     setFilterStatus(filterList[changedColumnIndex]);
        // }
    }

    return (
        <div className="m-sm-30">
            <Grid container spacing={3} direction="row" justifyContent="space-evenly">
                <Grid item xs={12}>
                    <Carousel
                        additionalTransfrom={0}
                        arrows
                        autoPlaySpeed={3000}
                        centerMode={false}
                        className=""
                        containerClass="container"
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
                                items: 2,
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
                            if (file && file?.id !== '') {
                                displayEl = <SLDashboardCard
                                    docObj={file}
                                    imagePath="illustrations"
                                    key={file.id}
                                    toggleEventHandler={() => toggleImageShowListing(file.id)}
                                    handleClickStatus={handleClickStatus}>
                                </SLDashboardCard>;
                            }
                            return displayEl;
                        })}

                    </Carousel>
                </Grid>
                <Grid item xs={12}>
                    {docFiles && docFiles.map(file => {
                        if (file && file?.id === selectedId) {
                            return file.listComponent({ roleId: user.role, filterStatus, setFilterStatus, onFilterChipClose: handleFilterChipClose, onFilterChange: handleFilterChange });
                        }

                        return null;
                    })}

                </Grid>
            </Grid>
        </div>
    );

};

SLDashboard.propTypes = {
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

export default SLDashboard;