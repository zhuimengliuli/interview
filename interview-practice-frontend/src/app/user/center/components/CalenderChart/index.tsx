"use client";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "./index.css";
import React, {useEffect, useState} from "react";
import ReactECharts from "echarts-for-react";
import dayjs from "dayjs";
import {getUserSignInRecordUsingPost} from "@/api/userController";
import {message} from "antd";

interface Props {}

/**
 * 刷题日历
 * @param props
 * @constructor
 */
const CalenderChart = (props: Props) => {
  const {} = props;
  const [dataList, setDataList] = useState<number[]>([1, 2, 300]);
  const year = new Date().getFullYear();

  const fetchDataList = async () => {
    try {
      const res = await getUserSignInRecordUsingPost({ year });
      if (res.data) {
        setDataList(res.data);
        console.log(res.data)
        message.success("获取签到记录成功");
      }
    } catch (e: any) {
      message.error("获取签到记录失败" + e.message);
    }
  }

  useEffect(() => {
    fetchDataList();
  }, []);

  const optionData = dataList.map((dayOfYear, index) => {
    const dayStr = dayjs(`${year}-01-01`)
      .add(dayOfYear - 1, "day")
      .format("YYYY-MM-DD");
    return [dayStr, 1];
  });
  const option = {
    visualMap: {
      show: false,
      min: 0,
      max: 1,
      inRange: ["#efefef", "lightgreen"],
    },
    calendar: {
      left: 20,
      cellSize: ["auto", 16],
      range: year,
      yearLabel: {
        position: "top",
        formatter: `${year}年刷题记录`,
      },
    },
    series: {
      type: "heatmap",
      coordinateSystem: "calendar",
      data: optionData,
    },
  };
  return <ReactECharts className="calender-chart" option={option} />;
};

export default CalenderChart;
