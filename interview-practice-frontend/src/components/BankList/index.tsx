"use client";
import "bytemd/dist/index.css";
import "highlight.js/styles/vs.css";
import "./index.css";
import { Avatar, Card, List, Typography } from "antd";
import Link from "next/link";

interface Props {
  bankList?: API.BankVO[];
}

/**
 * 题库列表组件d
 * @param props
 * @constructor
 */
const BankList = (props: Props) => {
  const { bankList = [] } = props;

  const bankView = (bank: API.BankVO) => {
    return (
      <Card>
        <Link href={`/bank/${bank.id}`}>
          <Card.Meta
            avatar={<Avatar src={bank.picture} />}
            title={bank.title}
            description={
              <Typography.Paragraph
                type="secondary"
                ellipsis={{ rows: 1 }}
                style={{ marginBottom: 0 }}
              >
                {bank.description}
              </Typography.Paragraph>
            }
          />
        </Link>
      </Card>
    );
  };

  return (
    <div className="bank-list">
      <List
        grid={{
          gutter: 16,
          column: 4,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
        }}
        dataSource={bankList}
        renderItem={(item) => <List.Item>{bankView(item)}</List.Item>}
      />
    </div>
  );
};

export default BankList;
