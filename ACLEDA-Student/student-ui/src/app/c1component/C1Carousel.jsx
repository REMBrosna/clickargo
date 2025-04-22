import React from "react";
import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css';
import Box from "@material-ui/core/Box";
import { makeStyles } from '@material-ui/core/styles';

const carouselStyles = makeStyles((theme) => ({
    root: {
        justifyContent: "center"
    },
}));

export default function C1Carousel({ children, isJustify }) {

    const carouseClasses = carouselStyles();

    return (
        <Box>
            <Carousel
                additionalTransfrom={0}
                arrows
                autoPlaySpeed={3000}
                centerMode={false}
                className={isJustify ? carouseClasses.root : ''}
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

                {children}

            </Carousel>
        </Box>
    );
};
