// @ts-ignore
/* eslint-disable */
import request from "@/libs/request";

/** addBank POST /api/bank/add */
export async function addBankUsingPost(
  body: API.BankAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>("/api/bank/add", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteBank POST /api/bank/delete */
export async function deleteBankUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/bank/delete", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** getBankVOById GET /api/bank/get/vo */
export async function getBankVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getBankVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBankVO_>("/api/bank/get/vo", {
    method: "GET",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listBankByPage POST /api/bank/list/page */
export async function listBankByPageUsingPost(
  body: API.BankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageBank_>("/api/bank/list/page", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** listBankVOByPage POST /api/bank/list/page/vo */
export async function listBankVoByPageUsingPost(
  body: API.BankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageBankVO_>("/api/bank/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyBankVOByPage POST /api/bank/my/list/page/vo */
export async function listMyBankVoByPageUsingPost(
  body: API.BankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageBankVO_>("/api/bank/my/list/page/vo", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** updateBank POST /api/bank/update */
export async function updateBankUsingPost(
  body: API.BankUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>("/api/bank/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
