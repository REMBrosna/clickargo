import React from "react";
import Carousel from 'react-multi-carousel';
import 'react-multi-carousel/lib/styles.css';

import C1Dialog from "app/c1component/C1Dialog";
import AnnouncementCard from "./AnnouncementCard";



const Announcement = ({ onOpen, onClose, data }) => {

    data = data || [];

    return (
        <C1Dialog title="Announcements" isOpen={onOpen.open} handleCloseEvent={onClose}
            maxWidth="md"
            showActions={false}
            scroll="body">
            <Carousel
                additionalTransfrom={0}
                arrows
                autoPlay
                infinite
                autoPlaySpeed={3000}
                centerMode={false}
                containerClass="first-carousel-container container"
                dotListClass=""
                draggable
                focusOnSelect={false}
                itemClass=""
                showDots
                keyBoardControl
                minimumTouchDrag={80}
                renderButtonGroupOutside={true}
                renderDotsOutside={true}
                responsive={{
                    desktop: {
                        breakpoint: {
                            max: 3000,
                            min: 1024
                        },
                        items: 1,
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
                        items: 1,
                        partialVisibilityGutter: 30
                    }
                }}
                sliderClass=""
                slidesToSlide={1}
                swipeable>

                {data.map((content, id) => (
                    <div id={id} key={id}><AnnouncementCard data={content} /></div>
                ))}

            </Carousel>


        </C1Dialog >

    );
};


export default Announcement;