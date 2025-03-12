import React from "react";
import { Grid, Card, IconButton, Icon } from "@material-ui/core";


const StatCard3 = () => {
  const statList = [
    {
      icon: "Assignment",
      amount: 300,
      title: "Applications Submitted",
    },
    {
      icon: "ArrowDownward",
      amount: 150,
      title: "Arrival Declarations",
    },
    {
      icon: "ArrowUpward",
      amount: 150,
      title: "Departure Declarations",
    },
    {
      icon: "CalendarViewDay",
      amount: 200,
      title: "Other Applications",
    },
  ];

  return (
    <div>
      <Grid container spacing={3}>
        {statList.map((item, ind) => (
          <Grid key={item.title} item md={3} sm={6} xs={12}>
            <Card elevation={3} className="p-5 flex">
              <div>
                <IconButton size="small" className="p-2 bg-light-gray">
                  <Icon className="text-muted">{item.icon}</Icon>
                </IconButton>
              </div>
              <div className="ml-4">
                <h3 className="mt-1 text-32">{item.amount.toLocaleString()}</h3>
                <p className="m-0 text-muted">{item.title}</p>
              </div>
            </Card>
          </Grid>
        ))}
      </Grid>
    </div>
  );
};

export default StatCard3;
