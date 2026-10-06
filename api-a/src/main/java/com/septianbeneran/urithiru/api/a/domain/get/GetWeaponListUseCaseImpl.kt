package com.septianbeneran.urithiru.api.a.domain.get

import com.septianbeneran.urithiru.api.a.repository.WeaponRepository
import com.septianbeneran.urithiru.core.entity.a.Weapon
import com.septianbeneran.urithiru.core.remote.entity.ApiResult
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