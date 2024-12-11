"use server";
import "./index.css";
import Title from "antd/es/typography/Title";
import { message } from "antd";
import {
  listQuestionVoByPageUsingPost,
  searchQuestionVoByPageUsingPost,
} from "@/api/questionController";
import QuestionTable from "@/components/QuestionTable";

/**
 * 题库列表页面
 * @constructor
 */
export default async function QuestionsPage({ searchParams }) {
  const { q: searchText } = searchParams;
  let questionList= [];
  let total = 0;
  try {
    const res = await searchQuestionVoByPageUsingPost({
      searchText,
      pageSize: 12,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionList = res.data.records ?? [];
    total = res.data.total ?? 0;
  } catch (e: any) {
    message.error("通过ES获取题目列表失败，" + e.message);
  }

  return (
    <div id="questionsPage" className="max-width-content">
      <Title level={3}>题目大全</Title>
      <QuestionTable
        defaultQuestionList={questionList}
        defaultTotal={total}
        defaultSearchParams={{
          title: searchText,
        }}
      />
    </div>
  );
}
