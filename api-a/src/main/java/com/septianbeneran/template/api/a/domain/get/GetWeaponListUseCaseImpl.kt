package com.septianbeneran.template.api.a.domain.get

import com.septianbeneran.template.api.a.data.repository.WeaponRepository
import com.septianbeneran.template.core.entity.a.Weapon
import com.septianbeneran.template.core.remote.entity.ApiResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeaponListUseCaseImpl @Inject constructor(
    private val repo: WeaponRepository
) : GetWeaponListUseCase {
    override fun invoke(
        limit: Int?,
        name: String?,
        page: Int?
    ): Flow<ApiResult<List<Weapon>>> = repo.getWeaponList(limit, name, page)
}