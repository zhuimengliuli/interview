"use client";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "./index.css";
import { Card, List } from "antd";
import Link from "next/link";
import TagList from "@/components/TagList";

interface Props {
  questionList?: API.QuestionVO[];
  cardTitle?: string;
    bankId?: number;
}

/**
 * 题目列表组件d
 * @param props
 * @constructor
 */
const QuestionList = (props: Props) => {
  const { questionList = [], cardTitle, bankId } = props;

  return (
    <Card className="question-list" title={cardTitle}>
      <List
        dataSource={questionList}
        renderItem={(item) => (
          <List.Item extra={<TagList tagList={item.tagList} />}>
            <List.Item.Meta
              title={<Link href={bankId ? `/bank/${bankId}/question/${item.id}` : `/question/${item.id}`}>{item.title}</Link>}
            />
          </List.Item>
        )}
      />
    </Card>
  );
};

export default QuestionList;
