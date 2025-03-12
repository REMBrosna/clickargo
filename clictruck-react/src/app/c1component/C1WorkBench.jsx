import React, { useState, useEffect } from "react";
import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css';
import C1DocumentCard from "app/c1component/C1DocumentCard";
import Box from "@material-ui/core/Box";
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';

const carouselStyles = makeStyles((theme) => ({
    root: {
        justifyContent: "center"
    },
}));

/**
 * @deprecated to be removed and will be replaced by C1Carousel as implementation
 * for vessel call (shipside) and workbench (portside)
 */
export default function C1WorkBench({ docs }) {

    const [docFiles, setDocFiles] = useState([]);

    const carouseClasses = carouselStyles();

    useEffect(() => {

        if (docFiles.length !== docs.length) {
            docs.map(d => {
                setDocFiles(f => {
                    let img = d.docType + '_x.png';
                    if (d.status > 0) {
                        img = d.docType + '_o.png';
                    }
                    return [...f, { img: img, ...d }]
                });
            });
        }
    }, [docs]);

    console.log("docfiles", docs, docFiles);

    return (
        <Box>
            <Carousel
                additionalTransfrom={0}
                arrows
                autoPlaySpeed={3000}
                centerMode={false}
                className={docFiles.length < 3 ? carouseClasses.root : ""}
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
                {docFiles.map(file => {
                    let displayEl = null;
                    if (file.id !== '') {
                        displayEl = <div id={file.id}><C1DocumentCard docObj={file} key={file.id}></C1DocumentCard> </div>;
                    }
                    return displayEl;
                })}

            </Carousel>
        </Box>
    );

};

C1WorkBench.propTypes = {
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