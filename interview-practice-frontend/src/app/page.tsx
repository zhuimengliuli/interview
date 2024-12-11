"use server"
import "./index.css";
import Title from "antd/es/typography/Title";
import {Divider, Flex, message} from "antd";
import Link from "next/link";
import BankList from "@/components/BankList";
import QuestionList from "@/components/QuestionList";
import {listBankVoByPageUsingPost} from "@/api/bankController";
import {listQuestionVoByPageUsingPost} from "@/api/questionController";

/**
 * 主页
 * @constructor
 */
export default async function HomePage() {
    let bankList = [];
    let questionList = [];
    try {
        const res = await listBankVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend",
        });
        bankList = res.data.records ?? [];
    } catch (e: any) {
        message.error("获取题库列表失败，" + e.message);
    }

    try {
        const res = await listQuestionVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend",
        });
        questionList = res.data.records ?? [];
    } catch (e: any) {
        message.error("获取题目列表失败，" + e.message);
    }

  return (
    <div id="homePage" className="max-width-content">
      <Flex justify="space-between" align="center">
        <Title level={3}>最新题库</Title>
        <Link href={"/banks"}>查看更多</Link>
      </Flex>
        <BankList bankList={bankList}/>
      <Divider />
      <Flex justify="space-between" align="center">
        <Title level={3}>最新题目</Title>
        <Link href={"/questions"}>查看更多</Link>
      </Flex>
        <QuestionList questionList={questionList}/>
    </div>
  );
}
